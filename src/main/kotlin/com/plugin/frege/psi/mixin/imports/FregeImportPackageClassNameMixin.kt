package com.plugin.frege.psi.mixin.imports

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.FregeElementFactory
import com.plugin.frege.psi.FregeImportPackageName
import com.plugin.frege.psi.FregeTypes
import com.plugin.frege.psi.impl.FregeCompositeElementImpl
import com.plugin.frege.resolve.FregeReferenceBase
import com.plugin.frege.stubs.index.FregeClassNameIndex

open class FregeImportPackageClassNameMixin(node: ASTNode) : FregeCompositeElementImpl(node), PsiIdentifier {
    override fun getTokenType(): IElementType {
        return FregeTypes.IMPORT_PACKAGE_CLASS_NAME
    }

    override fun getReference(): PsiReference {
        return object : FregeReferenceBase(this, TextRange(0, textLength)) {
            override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
                val packageName = psiElement.parentOfType<FregeImportPackageName>() ?: return emptyList()
                val qualifiedNameLength = psiElement.textRange.endOffset - packageName.textOffset
                val qualifiedName = packageName.text.substring(0, qualifiedNameLength)
                val project = psiElement.project
                return FregeClassNameIndex.INSTANCE.findByName(
                    qualifiedName,
                    project,
                    GlobalSearchScope.everythingScope(project)
                )
            }

            override fun handleElementRename(name: String): PsiElement {
                return psiElement.replace(FregeElementFactory.createImportPackageClassName(psiElement.project, name))
            }
        }
    }
}
