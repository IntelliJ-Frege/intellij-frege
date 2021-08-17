package com.plugin.frege.resolve

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfTypes
import com.plugin.frege.psi.FregeAnnotationItem
import com.plugin.frege.psi.FregeFunctionLhs

open class FregeOperatorReferenceBase(element: PsiElement, range: TextRange) : FregeReferenceBase(element, range) {
    override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
        val namedElementOwner = listOfNotNull(namedElementOwner)
        return when {
            psiElement.parentOfTypes(FregeAnnotationItem::class, FregeFunctionLhs::class) != null -> {
                FregeResolveUtil.resolveBindingByNameElement(psiElement, incompleteCode).ifEmpty { namedElementOwner }
            }
            else -> {
                namedElementOwner.ifEmpty { FregeResolveUtil.findMethodsFromUsage(psiElement, incompleteCode) }
            }
        }
    }
}
