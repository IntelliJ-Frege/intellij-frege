package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.FregeElementFactory;
import com.plugin.frege.psi.FregeFunctionName;
import com.plugin.frege.psi.impl.FregeNamedElementImpl;
import com.plugin.frege.resolve.FregeFunctionNameReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FregeFunctionNameMixin extends FregeNamedElementImpl implements FregeFunctionName, PsiNameIdentifierOwner, PsiElement {

    public FregeFunctionNameMixin(@NotNull ASTNode node) {
        super(node);
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

    @Override
    public String getName() {
        return getNameIdentifier().getText();
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return this;
    }
}
