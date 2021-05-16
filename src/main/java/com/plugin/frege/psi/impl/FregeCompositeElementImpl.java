package com.plugin.frege.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiReference;
import com.intellij.usageView.UsageViewUtil;
import com.plugin.frege.FregeIcons;
import com.plugin.frege.psi.FregeCompositeElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@SuppressWarnings("UnstableApiUsage")
public abstract class FregeCompositeElementImpl extends ASTWrapperPsiElement implements FregeCompositeElement {
    public FregeCompositeElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @Nullable PsiReference getReference() {
        return null;
    }

    @Override
    public String toString() {
        return getNode().getElementType().toString();
    }

    @Override
    public ItemPresentation getPresentation() {
        final String text = UsageViewUtil.createNodeText(this);
        return new ItemPresentation() {
            @Override
            public @NlsSafe @NotNull String getPresentableText() {
                return text;
            }

            @Override
            public @NlsSafe @NotNull String getLocationString() {
                return getContainingFile().getName();
            }

            @Override
            public @Nullable Icon getIcon(boolean unused) {
                return FregeCompositeElementImpl.this.getIcon(0);
            }
        };
    }

    @Override
    public @Nullable Icon getIcon(int flags) {
        return FregeIcons.FILE;
    }
}
