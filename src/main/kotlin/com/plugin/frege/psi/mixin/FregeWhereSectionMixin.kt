package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.psi.FregeScopeElement
import com.plugin.frege.psi.FregeSubprogramsHolder
import com.plugin.frege.psi.FregeWhereSection
import com.plugin.frege.psi.impl.FregeCompositeElementImpl

abstract class FregeWhereSectionMixin(node: ASTNode) : FregeCompositeElementImpl(node), FregeScopeElement, FregeWhereSection {
    override fun getSubprogramsFromScope(): List<PsiElement> {
        return PsiTreeUtil.getChildrenOfType(indentSection, FregeSubprogramsHolder::class.java)?.toList()
            ?: emptyList()
    }
}
