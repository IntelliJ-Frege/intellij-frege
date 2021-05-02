package com.plugin.frege.resolve;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FregePackageClassNameReference extends FregeReferenceBase {
    public FregePackageClassNameReference(@NotNull PsiElement element) {
        super(element, element.getTextRange());
    }

    @Override
    protected List<PsiElement> resolveInner(boolean incompleteCode) {
        return List.of(element);
    }
}
