package com.plugin.frege.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FregeFunctionNameReference extends FregeReferenceBase {
    public FregeFunctionNameReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    protected List<PsiElement> resolveInner(boolean incompleteCode) {
        return List.of(); // TODO
    }

    public static PsiReferenceProvider getReferenceProvider() {
        return new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                TextRange textRange = new TextRange(element.getTextOffset(), element.getTextOffset() + element.getTextLength());
                return new PsiReference[] { new FregeFunctionNameReference(element, textRange) };
            }
        };
    }
}
