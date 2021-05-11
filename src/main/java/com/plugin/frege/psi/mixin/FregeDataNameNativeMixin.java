package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.FregeElementFactory;
import com.plugin.frege.psi.impl.FregeNamedElementImpl;
import com.plugin.frege.resolve.FregeDataNameNativeReference;
import org.jetbrains.annotations.NotNull;

public class FregeDataNameNativeMixin extends FregeNamedElementImpl {
    public FregeDataNameNativeMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(@NlsSafe @NotNull String name) throws IncorrectOperationException {
        return getNameIdentifier().replace(FregeElementFactory.createDataNameNative(getProject(), name));
    }

    @Override
    public PsiReference getReference() {
        return new FregeDataNameNativeReference(this);
    }
}
