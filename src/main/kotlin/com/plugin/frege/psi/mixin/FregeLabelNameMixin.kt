package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiReference
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.FregeElementFactory
import com.plugin.frege.psi.FregeLabel
import com.plugin.frege.psi.FregeTypes
import com.plugin.frege.psi.impl.FregeCompositeElementImpl
import com.plugin.frege.resolve.FregeReferenceBase

open class FregeLabelNameMixin(node: ASTNode) : FregeCompositeElementImpl(node), PsiIdentifier {
    override fun getTokenType(): IElementType {
        return FregeTypes.LABEL_NAME
    }

    override fun getReference(): PsiReference {
        return object : FregeReferenceBase(this, TextRange(0, textLength)) {
            override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
                val label = psiElement.parentOfType<FregeLabel>()
                return if (label != null) listOf(label) else emptyList()
            }

            override fun handleElementRename(name: String): PsiElement {
                return psiElement.replace(FregeElementFactory.createLabelName(psiElement.project, name))
            }
        }
    }
}
