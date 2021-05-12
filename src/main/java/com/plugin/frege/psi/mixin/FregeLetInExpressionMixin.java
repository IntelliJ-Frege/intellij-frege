package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.psi.FregeIndentSection;
import com.plugin.frege.psi.FregeLetInExpression;
import com.plugin.frege.psi.FregeScopeElement;
import com.plugin.frege.psi.FregeSubprogramsHolder;
import com.plugin.frege.psi.impl.FregeCompositeElementImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FregeLetInExpressionMixin extends FregeCompositeElementImpl implements FregeScopeElement {
    public FregeLetInExpressionMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull List<PsiElement> getSubprogramsFromScope() {
        if (!(this instanceof FregeLetInExpression)) {
            return List.of();
        }
        FregeIndentSection indentSection = ((FregeLetInExpression) this).getLetExpression().getIndentSection();
        return PsiTreeUtil.getChildrenOfAnyType(indentSection, FregeSubprogramsHolder.class);
    }

    @Override
    public PsiReference getReference() {
        return null;
    }
}
