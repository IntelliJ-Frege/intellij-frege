package com.plugin.frege.psi.mixin.indentsection

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeCaseDecl
import com.plugin.frege.psi.FregeWeakScopeElement
import com.plugin.frege.psi.impl.FregeCompositeElementImpl

abstract class FregeCaseDeclMixin(node: ASTNode) : FregeCompositeElementImpl(node), FregeWeakScopeElement, FregeCaseDecl {
    override fun getSubprogramsFromScope(): List<PsiElement> {
        return whereSection?.linearIndentSection?.subprogramsFromScope ?: emptyList()
    }
}
