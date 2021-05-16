package com.plugin.frege.resolve

import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeFunctionName
import com.plugin.frege.psi.impl.FregePsiUtilImpl

class FregeFunctionNameReference(element: PsiElement) : FregeReferenceBase(element, element.textRange) {
    // TODO improve
    override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
        return FregePsiUtilImpl.findElementsWithinScope(
            psiElement,
            FregePsiUtilImpl.getByTypePredicateCheckingText(FregeFunctionName::class, psiElement, incompleteCode)
        )
    }
}
