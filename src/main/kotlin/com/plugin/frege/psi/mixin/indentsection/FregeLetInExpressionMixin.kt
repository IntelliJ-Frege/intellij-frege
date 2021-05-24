package com.plugin.frege.psi.mixin.indentsection

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.psi.FregeLetInExpression
import com.plugin.frege.psi.FregeScopeElement
import com.plugin.frege.psi.FregeSubprogramsHolder
import com.plugin.frege.psi.impl.FregeCompositeElementImpl

abstract class FregeLetInExpressionMixin(node: ASTNode) : FregeCompositeElementImpl(node), FregeScopeElement, FregeLetInExpression {
    override fun getSubprogramsFromScope(): List<PsiElement> {
        return letExpression.linearIndentSection?.subprogramsFromScope ?: emptyList()
    }
}
