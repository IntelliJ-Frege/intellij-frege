package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiReference
import com.intellij.psi.tree.IElementType
import com.plugin.frege.psi.FregeBinding
import com.plugin.frege.psi.FregeElementFactory
import com.plugin.frege.psi.FregeTypes
import com.plugin.frege.psi.impl.FregeCompositeElementImpl
import com.plugin.frege.psi.impl.FregePsiUtilImpl
import com.plugin.frege.resolve.FregeReferenceBase

open class FregeFunctionNameMixin(node: ASTNode) : FregeCompositeElementImpl(node), PsiIdentifier {
    override fun getReference(): PsiReference {
        return object : FregeReferenceBase(this, TextRange(0, textLength)) {
            override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
                val firstBinding = FregePsiUtilImpl.findElementsWithinScope(
                    psiElement,
                    FregePsiUtilImpl.getByTypePredicateCheckingName(FregeBinding::class, psiElement.text, incompleteCode)
                ).minByOrNull { it.textOffset }

                return if (firstBinding != null) listOf(firstBinding) else emptyList()
            }

            override fun handleElementRename(name: String): PsiElement {
                return psiElement.replace(FregeElementFactory.createFunctionName(psiElement.project, name))
            }
        }
    }

    override fun getTokenType(): IElementType {
        return FregeTypes.FUNCTION_NAME
    }
}
