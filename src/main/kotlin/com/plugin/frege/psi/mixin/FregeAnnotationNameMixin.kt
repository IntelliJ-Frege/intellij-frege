package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiReference
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.parentOfTypes
import com.plugin.frege.psi.FregeElementFactory
import com.plugin.frege.psi.FregeTypes
import com.plugin.frege.psi.impl.FregeAnnotationImpl
import com.plugin.frege.psi.impl.FregeAnnotationNameImpl
import com.plugin.frege.psi.impl.FregeCompositeElementImpl
import com.plugin.frege.resolve.FregeReferenceBase

open class FregeAnnotationNameMixin(node: ASTNode) : FregeCompositeElementImpl(node), PsiIdentifier {
    override fun getReference(): PsiReference? {
        return object : FregeReferenceBase(this, TextRange(0, textLength)) {
            // TODO incomplete code
            override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
                val annotationName = (psiElement as? FregeAnnotationNameImpl) ?: return emptyList()
                val annotation = annotationName.parentOfTypes(FregeAnnotationImpl::class, withSelf = false)
                val binding = annotation?.getBinding()
                return if (binding != null) listOf(binding) else emptyList()
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
