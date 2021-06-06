package com.plugin.frege.resolve

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.FregeElementFactory.createConidUsage
import com.plugin.frege.psi.FregeMainPackageClass
import com.plugin.frege.psi.FregePackageName
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.impl.FregePsiUtilImpl.findImportsNamesForElement
import com.plugin.frege.psi.impl.FregePsiUtilImpl.mergeQualifiedNames
import com.plugin.frege.resolve.FregeResolveUtil.findClassesByQualifiedName
import com.plugin.frege.resolve.FregeResolveUtil.findClassesInCurrentFile
import com.plugin.frege.resolve.FregeResolveUtil.findMethodsFromUsage
import com.plugin.frege.stubs.index.FregeClassNameIndex

class FregeConidUsageReference(element: PsiElement) : FregeReferenceBase(element, TextRange(0, element.textLength)) {
    override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
        if (psiElement.parentOfType<FregeMainPackageClass>() != null) {
            return resolveClassInPackage() // TODO support incomplete code
        }
        // TODO take into account qualified names
        val currentFileData = tryFindClassesInCurrentFile(incompleteCode).toMutableList()
        if (currentFileData.isEmpty() || incompleteCode) {
            currentFileData.addAll(tryFindClassesByImports()) // TODO support incomplete code
        }
        if (currentFileData.isEmpty() || incompleteCode) {
            currentFileData.addAll(findMethodsFromUsage(psiElement, incompleteCode))
        }
        return currentFileData
    }

    override fun bindToElement(element: PsiElement): PsiElement {
        return if (element is PsiNamedElement) {
            val newName = element.name
            val oldName = psiElement.text
            if (newName != null && oldName != newName) {
                return handleElementRename(newName)
            } else {
                psiElement
            }
        } else {
            super.bindToElement(element)
        }
    }

    override fun handleElementRename(name: String): PsiElement {
        return psiElement.replace(createConidUsage(psiElement.project, name))
    }

    private fun resolveClassInPackage(): List<PsiElement> {
        val packageName = psiElement.parentOfType<FregePackageName>() ?: return emptyList()
        val qualifiedNameLength = psiElement.textRange.endOffset - packageName.textOffset
        val qualifiedName = packageName.text.substring(0, qualifiedNameLength)
        val project = psiElement.project
        return FregeClassNameIndex.INSTANCE.findByName(
            qualifiedName,
            project,
            GlobalSearchScope.everythingScope(project)
        )
    }

    private fun tryFindClassesInCurrentFile(incompleteCode: Boolean): List<PsiElement> {
        val referenceText = psiElement.text
        val classes: MutableList<FregePsiClass> = findClassesInCurrentFile(psiElement).toMutableList()
        if (!incompleteCode) {
            classes.removeIf { referenceText != it.name }
        }
        return classes
    }

    private fun tryFindClassesByImports(): List<PsiElement> {
        val className = psiElement.text
        val project = psiElement.project
        val imports = findImportsNamesForElement(psiElement, true)
        for (currentImport in imports) {
            val qualifiedName = mergeQualifiedNames(currentImport, className)
            val classes = findClassesByQualifiedName(project, qualifiedName)
            if (classes.isNotEmpty()) {
                return classes
            }
        }
        return emptyList()
    }
}
