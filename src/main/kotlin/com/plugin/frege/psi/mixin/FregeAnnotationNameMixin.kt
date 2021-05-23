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
import com.plugin.frege.resolve.FregeReferenceBase
import com.plugin.frege.resolve.FregeResolveUtil.resolveBindingByNameElement

open class FregeAnnotationNameMixin(node: ASTNode) : FregeCompositeElementImpl(node), PsiIdentifier {
    override fun getReference(): PsiReference? {
        return object : FregeReferenceBase(this, TextRange(0, textLength)) {
            // TODO incomplete code
            override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
                return resolveBindingByNameElement(psiElement, incompleteCode)
            }

            override fun handleElementRename(name: String): PsiElement {
                return psiElement.replace(FregeElementFactory.createAnnotationName(project, name))
            }
        }
    }

    override fun getTokenType(): IElementType {
        return FregeTypes.ANNOTATION_NAME
    }
}
