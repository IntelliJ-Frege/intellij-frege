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

open class FregeVaridUsageImportMixin(node: ASTNode) : FregeCompositeElementImpl(node), FregeResolvableElement {
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
                val name: String = psiElement.text
                val firstQualifier: String?
                val secondQualifier: String?
                when (val parent = psiElement.parent) {
                    is FregeQVaridUsageImport -> {
                        firstQualifier = parent.conidUsageImportList.firstOrNull()?.text
                        secondQualifier = parent.conidUsageImportList.getOrNull(1)?.text
                    }
                    is FregeImportItem -> {
                        firstQualifier = null
                        secondQualifier = parent.conidUsageImport?.text
                    }
                    else -> { // TODO support importMembers
                        firstQualifier = null
                        secondQualifier = null
                    }
                }
                return FregeImportResolveUtil.findMethodsByNameInImports(
                    name, firstQualifier, secondQualifier, module, listOf(fakeImport)
                )
            }

            override fun handleElementRename(name: String): PsiElement {
                return psiElement.replace(FregeElementFactory.createVaridUsageImport(project, name))
            }
        }
    }
}
