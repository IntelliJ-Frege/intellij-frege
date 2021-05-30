package com.plugin.frege.psi.mixin.indentsection

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeNestedIndentSectionItemsSemicolon
import com.plugin.frege.psi.FregeNestedIndentSectionItemsVirtual
import com.plugin.frege.psi.FregeScopeElement
import com.plugin.frege.psi.impl.FregeCompositeElementImpl

open class FregeNestedIndentSectionItemsMixin(node: ASTNode) :
    FregeCompositeElementImpl(node), FregeScopeElement {

    override fun getSubprogramsFromScope(): List<PsiElement> {
        return children.asSequence()
            .filter { it !is FregeNestedIndentSectionItemsVirtual && it !is FregeNestedIndentSectionItemsSemicolon }
            .filterIsInstance<FregeScopeElement>()
            .flatMap { it.subprogramsFromScope }
            .toList()
    }
}
