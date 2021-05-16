package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeBody
import com.plugin.frege.psi.FregeScopeElement
import com.plugin.frege.psi.FregeTopDecl
import com.plugin.frege.psi.impl.FregeCompositeElementImpl

abstract class FregeBodyMixin(node: ASTNode) : FregeCompositeElementImpl(node), FregeScopeElement, FregeBody {
    override fun getSubprogramsFromScope(): List<PsiElement> {
        return topDeclList.mapNotNull { topDecl: FregeTopDecl -> topDecl.decl }
    }
}
