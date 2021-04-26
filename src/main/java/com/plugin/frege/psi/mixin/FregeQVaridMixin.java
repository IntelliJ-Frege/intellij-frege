package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.FregeQVarId;
import com.plugin.frege.psi.impl.FregeCompositeElementImpl;
import com.plugin.frege.resolve.FregeQVaridReference;
import org.jetbrains.annotations.NotNull;

public class FregeQVaridMixin extends FregeCompositeElementImpl implements FregeQVarId {
    public FregeQVaridMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new FregeQVaridReference(this);
    }

    @Override
    public PsiElement setName(@NlsSafe @NotNull String name) throws IncorrectOperationException {
        return this;
    }
}
