package com.plugin.frege.psi.mixin.indentsection

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeDoDecl
import com.plugin.frege.psi.FregeScopeElement
import com.plugin.frege.psi.impl.FregeCompositeElementImpl

abstract class FregeDoDeclMixin(node: ASTNode) : FregeCompositeElementImpl(node), FregeScopeElement, FregeDoDecl {
    override fun getSubprogramsFromScope(): List<PsiElement> {
        val letExpressionSubprograms = letExpression?.linearIndentSection?.subprogramsFromScope
        val pattern = pattern
        return when {
            letExpressionSubprograms != null -> letExpressionSubprograms
            pattern != null -> listOf(pattern)
            else -> emptyList()
        }
    }
}
