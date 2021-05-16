package com.plugin.frege.resolve

import com.intellij.psi.PsiElement

class FregeNamedElementReference(psiElement: PsiElement) : FregeReferenceBase(psiElement, psiElement.textRange) {
    override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
        return listOf(psiElement)
    }
}