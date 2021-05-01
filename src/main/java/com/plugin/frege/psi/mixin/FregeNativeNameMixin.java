package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.plugin.frege.psi.impl.FregeCompositeElementImpl;
import com.plugin.frege.resolve.FregeNativeNameReference;
import org.jetbrains.annotations.NotNull;

public class FregeNativeNameMixin extends FregeCompositeElementImpl {
    public FregeNativeNameMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new FregeNativeNameReference(this);
    }
}
