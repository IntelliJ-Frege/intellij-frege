package com.plugin.frege.psi.mixin.imports

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.plugin.frege.psi.FregeElementFactory
import com.plugin.frege.psi.FregePackagePrefix
import com.plugin.frege.psi.impl.FregeCompositeElementImpl
import com.plugin.frege.resolve.FregeReferenceBase

open class FregePackageTokenMixin(node: ASTNode) : FregeCompositeElementImpl(node) { // TODO implement psi package
    override fun getReference(): PsiReference {
        return object : FregeReferenceBase(this, TextRange(0, textLength)) {
            override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
                val packagePrefix = psiElement.parent as? FregePackagePrefix ?: return emptyList()
                val packageText = packagePrefix.text
                val qualifiedName = packageText.substring(0, psiElement.textRange.endOffset - parent.textOffset)
                val resolvedPackage = JavaPsiFacade.getInstance(psiElement.project).findPackage(qualifiedName)
                return if (resolvedPackage != null) listOf(resolvedPackage) else emptyList()
            }

            override fun handleElementRename(name: String): PsiElement {
                return psiElement.replace(FregeElementFactory.createPackageToken(psiElement.project, name))
            }
        }
    }
}
