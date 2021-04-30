package com.plugin.frege.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.plugin.frege.psi.FregeFunctionName;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.plugin.frege.psi.impl.FregePsiUtilImpl.findElementsWithinScope;
import static com.plugin.frege.psi.impl.FregePsiUtilImpl.scopeOfElement;

public class FregeFunctionNameReference extends FregeReferenceBase {
    public FregeFunctionNameReference(@NotNull PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
    }

    // TODO improve
    @Override
    protected List<PsiElement> resolveInner(boolean incompleteCode) {
        PsiElement scope = scopeOfElement(element);
        if (scope == null) {
            return List.of();
        }

        String referenceText = element.getText();
        return findElementsWithinScope(element,
                elem -> elem instanceof FregeFunctionName && elem.getText().equals(referenceText));
    }
}
