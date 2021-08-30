package com.plugin.frege.psi.mixin.imports

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.FregeImportDecl
import com.plugin.frege.psi.FregeImportDeclAlias
import com.plugin.frege.psi.impl.LightFregePsiClassBase
import com.plugin.frege.resolve.FregeImportResolveUtil

@Suppress("UnstableApiUsage")
abstract class FregeImportDeclAliasMixin(node: ASTNode) : LightFregePsiClassBase(node), FregeImportDeclAlias {
    override val delegate
        get(): PsiClass? = parentOfType<FregeImportDecl>()?.let { import ->
            FregeImportResolveUtil.getModuleByImport(import)
        }

    override fun getNameIdentifier(): PsiIdentifier? = conidUsage
}
