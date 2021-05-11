package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.plugin.frege.psi.FregeProgram;
import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.psi.FregePsiClassHolder;
import com.plugin.frege.psi.impl.FregeCompositeElementImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FregeProgramMixin extends FregeCompositeElementImpl implements FregePsiClassHolder {
    public FregeProgramMixin(@NotNull ASTNode node) {
        super(node);
        if (!(this instanceof FregeProgram)) {
            throw new IllegalStateException("This element must be an instance of Frege Program");
        }
    }

    @Override
    public @NotNull FregePsiClass getHoldingClass() {
        return Objects.requireNonNull(((FregeProgram) this).getPackageName()).getPackageClassName();
    }

    @Override
    public PsiReference getReference() {
        return null;
    }
}