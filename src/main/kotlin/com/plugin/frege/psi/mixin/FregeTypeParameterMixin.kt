package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.FregeElementFactory
import com.plugin.frege.psi.FregeTypeParametersHolder
import com.plugin.frege.psi.impl.FregeCompositeElementImpl
import com.plugin.frege.resolve.FregeReferenceBase

open class FregeTypeParameterMixin(node: ASTNode) : FregeCompositeElementImpl(node) {
    override fun getReference(): PsiReference {
        return object : FregeReferenceBase(this, TextRange(0, textLength)) {
            override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
                val referenceText = psiElement.text
                var result: PsiElement? = null
                var currentHolder = psiElement.parentOfType<FregeTypeParametersHolder>()
                while (currentHolder != null) {
                    val typeParameter = currentHolder.typedVaridDeclarations.find { it.text == referenceText }
                    if (typeParameter != null) {
                        result = typeParameter
                    }
                    currentHolder = currentHolder.parentOfType()
                }

                return listOfNotNull(result)
            }

            override fun handleElementRename(name: String): PsiElement {
                return psiElement.replace(FregeElementFactory.createTypeParameter(psiElement.project, name))
            }
        }
    }
}
