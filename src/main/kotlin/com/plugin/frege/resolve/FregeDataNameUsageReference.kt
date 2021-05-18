package com.plugin.frege.resolve

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.psi.FregeDataNameNative
import com.plugin.frege.psi.FregeElementFactory.createDataNameUsage
import com.plugin.frege.psi.impl.FregePsiClassUtilImpl.getClassesByQualifiedName
import com.plugin.frege.psi.impl.FregePsiUtilImpl.findAvailableDataDecls
import com.plugin.frege.psi.impl.FregePsiUtilImpl.findImportsNamesForElement
import com.plugin.frege.psi.impl.FregePsiUtilImpl.keepWithText

class FregeDataNameUsageReference(element: PsiElement) : FregeReferenceBase(element, TextRange(0, element.textLength)) {
    public override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
        val currentFileData = tryFindDataInCurrentFile(incompleteCode)
        return currentFileData.ifEmpty { tryFindDataByImports() } // TODO support incomplete code
    }

    override fun handleElementRename(name: String): PsiElement {
        return psiElement.replace(createDataNameUsage(psiElement.project, name))
    }

    private fun tryFindDataInCurrentFile(incompleteCode: Boolean): List<PsiElement> {
        val referenceText = psiElement.text
        val dataInCurrentFile: MutableList<PsiElement> = findAvailableDataDecls(psiElement)
            .mapNotNull { PsiTreeUtil.findChildOfType(it, FregeDataNameNative::class.java) }
            .toMutableList()

        if (!incompleteCode) {
            dataInCurrentFile.removeIf { !keepWithText(referenceText).invoke(it) }
        }
        return dataInCurrentFile
    }

    private fun tryFindDataByImports(): List<PsiElement> {
        val className = psiElement.text
        val project = psiElement.project
        val imports = findImportsNamesForElement(psiElement, true)
        for (currentImport in imports) {
            val qualifiedName = "$currentImport.$className"
            val classes = getClassesByQualifiedName(project, qualifiedName)
            if (classes.isNotEmpty()) {
                return classes
            }
        }
        return emptyList()
    }
}
