package com.plugin.frege.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Scope is a {@link PsiElement} that has a list of subprograms
 */
public interface FregeScopeElement extends PsiElement {
    @NotNull List<PsiElement> getSubprogramsFromScope();
}
