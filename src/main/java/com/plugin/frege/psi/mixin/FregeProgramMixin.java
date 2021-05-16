package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.plugin.frege.psi.FregePackageName;
import com.plugin.frege.psi.FregeProgram;
import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.psi.FregePsiClassHolder;
import com.plugin.frege.psi.impl.FregeCompositeElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FregeProgramMixin extends FregeCompositeElementImpl implements FregePsiClassHolder, FregeProgram {
    public FregeProgramMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @Nullable FregePsiClass getHoldingClass() {
        FregePackageName packageName = getPackageName();
        return packageName == null ? null : packageName.getPackageClassName();
    }
}
