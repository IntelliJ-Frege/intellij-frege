package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.FregeAnnoItem;
import com.plugin.frege.psi.FregeElementFactory;
import com.plugin.frege.psi.FregeFunLhs;
import com.plugin.frege.psi.impl.FregeNamedElementImpl;
import com.plugin.frege.resolve.FregeFunctionNameReference;
import org.jetbrains.annotations.NotNull;

public abstract class FregeFunctionNameMixin extends FregeNamedElementImpl {
    public FregeFunctionNameMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(@NlsSafe @NotNull String name) throws IncorrectOperationException {
        return getNameIdentifier().replace(FregeElementFactory.createFunctionName(getProject(), name));
    }

    @Override
    public PsiReference getReference() {
        return new FregeFunctionNameReference(this);
    }

    public boolean isFunctionBinding() {
        return getParent() instanceof FregeFunLhs;
    }

    public boolean isFunctionAnnotation() {
        return getParent() instanceof FregeAnnoItem;
    }
}
