package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiReference
import com.intellij.psi.tree.IElementType
import com.plugin.frege.psi.FregeElementFactory
import com.plugin.frege.psi.FregeTypes
import com.plugin.frege.psi.impl.FregeCompositeElementImpl
import com.plugin.frege.resolve.FregeOperatorReferenceBase

open class FregeWordOperatorMixin(node: ASTNode) : FregeCompositeElementImpl(node), PsiIdentifier {
    override fun getReference(): PsiReference {
        return object : FregeOperatorReferenceBase(this, TextRange(0, textLength)) {
            override fun handleElementRename(name: String): PsiElement {
                return psiElement.replace(FregeElementFactory.createWordOperator(psiElement.project, name))
            }
        }
    }

    override fun getTokenType(): IElementType = FregeTypes.WORD_OPERATOR
}
