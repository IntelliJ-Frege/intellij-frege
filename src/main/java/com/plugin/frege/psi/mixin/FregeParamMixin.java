package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.FregeElementFactory;
import com.plugin.frege.psi.impl.FregeNamedElementImpl;
import org.jetbrains.annotations.NotNull;

public class FregeParamMixin extends FregeNamedElementImpl {
    public FregeParamMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return getNameIdentifier().replace(FregeElementFactory.createParam(getProject(), name));
    }
}
