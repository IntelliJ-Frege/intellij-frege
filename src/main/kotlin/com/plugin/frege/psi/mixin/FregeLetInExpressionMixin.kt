package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.psi.FregeLetInExpression
import com.plugin.frege.psi.FregeScopeElement
import com.plugin.frege.psi.FregeSubprogramsHolder
import com.plugin.frege.psi.impl.FregeCompositeElementImpl

abstract class FregeLetInExpressionMixin(node: ASTNode) : FregeCompositeElementImpl(node), FregeScopeElement, FregeLetInExpression {
    override fun getSubprogramsFromScope(): List<PsiElement> {
        val indentSection = letExpression.indentSection
        return PsiTreeUtil.getChildrenOfType(indentSection, FregeSubprogramsHolder::class.java).toList()
    }

    override fun getReference(): PsiReference? {
        return null
    }
}
