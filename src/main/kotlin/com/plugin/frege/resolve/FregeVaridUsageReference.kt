package com.plugin.frege.resolve

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.FregeDoDecl
import com.plugin.frege.psi.FregeElementFactory.createVaridUsage
import com.plugin.frege.psi.FregeName
import com.plugin.frege.psi.FregeParameter
import com.plugin.frege.psi.FregeParametersHolder
import com.plugin.frege.psi.impl.FregePsiUtilImpl.findElementsWithinElement
import com.plugin.frege.psi.impl.FregePsiUtilImpl.getPredicateCheckingTypeAndName
import com.plugin.frege.psi.impl.FregePsiUtilImpl.scopeOfElement

class FregeVaridUsageReference(element: PsiElement) : FregeReferenceBase(element, TextRange(0, element.textLength)) {

    override fun handleElementRename(name: String): PsiElement {
        return psiElement.replace(createVaridUsage(psiElement.project, name))
    }

    override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
        val namedElementOwner = namedElementOwner
        if (namedElementOwner != null) {
            return listOf(namedElementOwner)
        }

        val result = findParameters(incompleteCode).toMutableList()
        if (result.isNotEmpty() && !incompleteCode) {
            return result
        }
        result.addAll(FregeResolveUtil.findMethodsFromUsage(psiElement, incompleteCode))
        return result
    }

    private fun findParameters(incompleteCode: Boolean): List<PsiElement> {
        val predicate = getPredicateCheckingTypeAndName(FregeParameter::class, FregeName(psiElement), incompleteCode)
        var paramHolder = psiElement.parentOfType<FregeParametersHolder>(false)
        while (paramHolder != null) {
            val params = findElementsWithinElement(paramHolder, predicate)
            if (params.isNotEmpty()) {
                return params
            }
            paramHolder = paramHolder.parentOfType(false)
        }

        return findParametersInDoDecls(incompleteCode)
    }

    private fun findParametersInDoDecls(incompleteCode: Boolean): List<PsiElement> {
        val predicate = getPredicateCheckingTypeAndName(FregeParameter::class, FregeName(psiElement), incompleteCode)
        var scope = scopeOfElement(psiElement)
        while (scope != null) {
            val doDecl = PsiTreeUtil.getPrevSiblingOfType(scope, FregeDoDecl::class.java)
            if (doDecl?.pattern != null) {
                val params = findElementsWithinElement(doDecl.pattern, predicate)
                if (params.isNotEmpty()) {
                    return params
                }
            }

            scope = scopeOfElement(scope.parent)
        }

        return emptyList()
    }
}
