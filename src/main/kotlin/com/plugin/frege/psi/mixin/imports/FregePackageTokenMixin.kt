package com.plugin.frege.psi.mixin.imports

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.util.containers.addIfNotNull
import com.plugin.frege.psi.FregeElementFactory
import com.plugin.frege.psi.FregePackagePrefix
import com.plugin.frege.psi.impl.FregeCompositeElementImpl
import com.plugin.frege.psi.util.FregePsiUtil
import com.plugin.frege.resolve.FregeReferenceBase

open class FregePackageTokenMixin(node: ASTNode) : FregeCompositeElementImpl(node) { // TODO implement psi package
    override fun getReference(): PsiReference {
        return object : FregeReferenceBase(this, TextRange(0, textLength)) {
            override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
                val packagePrefix = psiElement.parent as? FregePackagePrefix ?: return emptyList()
                val packageText = packagePrefix.text
                val qualifiedName = packageText.substring(0, psiElement.textRange.endOffset - parent.textOffset)
                val results = mutableListOf<PsiElement>()
                val psiFacade = JavaPsiFacade.getInstance(psiElement.project)
                results.addIfNotNull(psiFacade.findPackage(qualifiedName))

                if (results.isEmpty()) {
                    val libraryPackage = FregePsiUtil.tryConvertToLibraryPackage(qualifiedName)
                    if (libraryPackage != null) {
                        results.addIfNotNull(psiFacade.findPackage(libraryPackage))
                    }
                }
                return results
            }

            override fun handleElementRename(name: String): PsiElement {
                return psiElement.replace(FregeElementFactory.createPackageToken(psiElement.project, name))
            }
        }
    }
}
