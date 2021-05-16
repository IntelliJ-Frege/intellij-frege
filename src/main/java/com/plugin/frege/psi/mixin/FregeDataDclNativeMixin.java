package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.plugin.frege.psi.FregeDataDclNative;
import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.psi.FregePsiClassHolder;
import com.plugin.frege.psi.impl.FregeCompositeElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FregeDataDclNativeMixin extends FregeCompositeElementImpl implements FregePsiClassHolder, FregeDataDclNative {
    public FregeDataDclNativeMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @Nullable FregePsiClass getHoldingClass() {
        return getDataNameNative();
    }

    @Override
    public PsiReference getReference() {
        return null;
    }
}
