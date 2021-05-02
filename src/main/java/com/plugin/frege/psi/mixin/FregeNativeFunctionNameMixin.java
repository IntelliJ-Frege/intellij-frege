package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.plugin.frege.psi.impl.FregeCompositeElementImpl;
import com.plugin.frege.resolve.FregeNativeFunctionNameReference;
import org.jetbrains.annotations.NotNull;

public class FregeNativeFunctionNameMixin extends FregeCompositeElementImpl {
    public FregeNativeFunctionNameMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new FregeNativeFunctionNameReference(this);
    }
}
