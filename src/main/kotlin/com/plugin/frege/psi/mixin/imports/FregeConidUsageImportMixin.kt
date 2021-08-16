package com.plugin.frege.psi.mixin.imports

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.*
import com.plugin.frege.psi.impl.FregeCompositeElementImpl
import com.plugin.frege.resolve.FregeImportResolveUtil
import com.plugin.frege.resolve.FregeReferenceBase

open class FregeConidUsageImportMixin(node: ASTNode) : FregeCompositeElementImpl(node), FregeResolvableElement {
    override fun getReference(): PsiReference {
        return object : FregeReferenceBase(this, TextRange(0, textLength)) {
            override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
                val import = psiElement.parentOfType<FregeImportDecl>() ?: return emptyList()
                val module = psiElement.parentOfType<FregeProgram>() ?: return emptyList()
                val importPackage = import.importPackageName ?: return emptyList()

                // creating a fake import without import list (escaping recursion)
                // it reuses logic for searching classes in regular imports
                val fakeImport = FregeElementFactory.createImportDeclByPackage(
                    psiElement.project, importPackage.text
                )
                return FregeImportResolveUtil.findClassesByNameInImports(
                    FregeName(psiElement), module, listOf(fakeImport)
                )
            }

            override fun handleElementRename(name: String): PsiElement {
                return psiElement.replace(FregeElementFactory.createConidUsageImport(psiElement.project, name))
            }
        }
    }
}
