package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.FregeElementFactory;
import com.plugin.frege.psi.FregeFunctionName;
import com.plugin.frege.psi.impl.FregeNamedElementImpl;
import com.plugin.frege.resolve.FregeFunctionNameReference;
import org.jetbrains.annotations.NotNull;

public abstract class FregeFunctionNameMixin extends FregeNamedElementImpl implements FregeFunctionName {
    public FregeFunctionNameMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        getNameIdentifier().replace(FregeElementFactory.createFunctionName(getProject(), name));
        return this;
    }

    @Override
    public PsiReference getReference() {
        return new FregeFunctionNameReference(this);
    }
}
