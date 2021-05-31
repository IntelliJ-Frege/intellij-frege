package com.plugin.frege.resolve

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeElementFactory.createDataNameUsage
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.impl.FregePsiUtilImpl.findImportsNamesForElement
import com.plugin.frege.psi.impl.FregePsiUtilImpl.mergeQualifiedNames
import com.plugin.frege.resolve.FregeResolveUtil.findClassesByQualifiedName
import com.plugin.frege.resolve.FregeResolveUtil.findClassesInCurrentFile
import com.plugin.frege.resolve.FregeResolveUtil.findMethodsFromUsage

class FregeConidUsageReference(element: PsiElement) : FregeReferenceBase(element, TextRange(0, element.textLength)) {
    public override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
        val currentFileData = tryFindClassesInCurrentFile(incompleteCode).toMutableList()
        if (currentFileData.isEmpty() || incompleteCode) {
            currentFileData.addAll(tryFindClassesByImports()) // TODO support incomplete code
        }
        if (currentFileData.isEmpty() || incompleteCode) {
            currentFileData.addAll(findMethodsFromUsage(psiElement, incompleteCode))
        }
        return currentFileData
    }

    override fun handleElementRename(name: String): PsiElement {
        return psiElement.replace(createDataNameUsage(psiElement.project, name))
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
