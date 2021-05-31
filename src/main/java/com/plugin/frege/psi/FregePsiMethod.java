package com.plugin.frege.psi;

import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public interface FregePsiMethod extends FregeNamedElement, PsiMethod {
    @Override
    @Nullable FregePsiClass getContainingClass();
}
