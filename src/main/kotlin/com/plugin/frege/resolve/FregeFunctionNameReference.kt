package com.plugin.frege.resolve

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeFunctionName
import com.plugin.frege.psi.impl.FregePsiUtilImpl

class FregeFunctionNameReference(element: PsiElement) : FregeReferenceBase(element, TextRange(0, element.textLength)) {
    // TODO improve
    override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
        val firstBinding = FregePsiUtilImpl.findElementsWithinScope(
            psiElement,
            FregePsiUtilImpl.getByTypePredicateCheckingText(FregeFunctionName::class, psiElement, incompleteCode)
        ).minByOrNull { it.textOffset }

        return if (firstBinding != null) listOf(firstBinding) else emptyList()
    }
}
