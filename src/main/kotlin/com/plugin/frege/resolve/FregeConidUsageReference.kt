package com.plugin.frege.resolve

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeElementFactory.createConidUsage
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.resolve.FregeResolveUtil.findClassesFromUsage
import com.plugin.frege.resolve.FregeResolveUtil.findMethodsFromUsage

class FregeConidUsageReference(element: PsiElement) : FregeReferenceBase(element, TextRange(0, element.textLength)) {
    override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
        namedElementOwner?.let { return listOf(it) }

        val results = findClassesFromUsage(psiElement, incompleteCode).toMutableList()
        if (incompleteCode || results.isEmpty()) {
            results.addAll(findMethodsFromUsage(psiElement, incompleteCode))
        }
        return results.filter { it !is FregePsiClass || it.canBeReferenced() }
    }

    override fun handleElementRename(name: String): PsiElement {
        return psiElement.replace(createConidUsage(psiElement.project, name))
    }
}
