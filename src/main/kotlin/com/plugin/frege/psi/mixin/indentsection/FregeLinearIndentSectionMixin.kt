package com.plugin.frege.psi.mixin.indentsection

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.psi.FregeLinearIndentSection
import com.plugin.frege.psi.FregeScopeElement
import com.plugin.frege.psi.FregeSubprogramsHolder
import com.plugin.frege.psi.impl.FregeCompositeElementImpl
import org.jetbrains.annotations.NotNull

abstract class FregeLinearIndentSectionMixin(node: @NotNull ASTNode) :
    FregeCompositeElementImpl(node), FregeScopeElement, FregeLinearIndentSection {

    override fun getSubprogramsFromScope(): List<PsiElement> {
        return PsiTreeUtil.getChildrenOfType(
            linearIndentSectionItemsSemicolon ?: linearIndentSectionItemsVirtual,
            FregeSubprogramsHolder::class.java
        )?.toList() ?: emptyList()
    }
}
