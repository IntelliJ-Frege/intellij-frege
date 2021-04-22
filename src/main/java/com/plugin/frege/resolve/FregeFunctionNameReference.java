package com.plugin.frege.resolve;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FregeFunctionNameReference extends FregeReferenceBase {
    public FregeFunctionNameReference(@NotNull PsiElement element) {
        super(element);
    }

    @Override
    protected List<PsiElement> resolveInner(boolean incompleteCode) {
        return List.of(); // TODO
    }
}
