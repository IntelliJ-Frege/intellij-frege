package com.plugin.frege.psi;

import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public interface FregePsiMethod extends FregeNamedElement, PsiMethod, FregeElementProvideDocumentation {
    @Override
    @Nullable FregePsiClass getContainingClass();

    /**
     * Indicates that this method can be searched only with class-name-qualifier.
     */
    boolean onlyQualifiedSearch();
}
