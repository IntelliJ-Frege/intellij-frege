package com.plugin.frege.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * Indicates that it must hold a {@link FregePsiClass} in children.
 * Helps to get a containing class for an element.
 */
public interface FregePsiClassHolder extends PsiElement {
    @Nullable
    FregePsiClass getHoldingClass();
}
