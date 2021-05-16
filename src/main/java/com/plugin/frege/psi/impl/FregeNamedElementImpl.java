package com.plugin.frege.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.plugin.frege.psi.FregeNamedElement;
import com.plugin.frege.resolve.FregeNamedElementReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FregeNamedElementImpl extends FregeCompositeElementImpl implements FregeNamedElement, PsiNameIdentifierOwner {
    public FregeNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public abstract @Nullable PsiElement getNameIdentifier();

    @Override
    public @Nullable String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier != null ? nameIdentifier.getText() : null;
    }

    @Override
    public @NotNull PsiElement getNavigationElement() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier != null ? nameIdentifier : this;
    }

    @Override
    public int getTextOffset() {
        PsiElement nameIdentifier = getNameIdentifier();
        if (nameIdentifier != null && nameIdentifier != this) {
            return nameIdentifier.getTextOffset();
        } else {
            return super.getTextOffset();
        }
    }

    @Override
    public PsiReference getReference() {
        return new FregeNamedElementReference(this);
    }
}
