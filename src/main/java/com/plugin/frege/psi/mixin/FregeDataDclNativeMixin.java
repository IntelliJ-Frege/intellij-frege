package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.plugin.frege.psi.FregeDataDclNative;
import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.psi.FregePsiClassHolder;
import com.plugin.frege.psi.impl.FregeCompositeElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FregeDataDclNativeMixin extends FregeCompositeElementImpl implements FregePsiClassHolder {
    public FregeDataDclNativeMixin(@NotNull ASTNode node) {
        super(node);
        if (!(this instanceof FregeDataDclNative)) {
            throw new IllegalStateException("This element must be an instance of Frege Program");
        }
    }

    @Override
    public @Nullable FregePsiClass getHoldingClass() {
        return ((FregeDataDclNative) this).getDataNameNative();
    }

    @Override
    public PsiReference getReference() {
        return null;
    }
}
