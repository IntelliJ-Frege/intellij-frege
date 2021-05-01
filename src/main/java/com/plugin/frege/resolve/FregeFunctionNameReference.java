package com.plugin.frege.resolve;

import com.intellij.psi.PsiElement;
import com.plugin.frege.psi.FregeFunctionName;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.plugin.frege.psi.impl.FregePsiUtilImpl.findElementsWithinScope;

public class FregeFunctionNameReference extends FregeReferenceBase {
    public FregeFunctionNameReference(@NotNull PsiElement element) {
        super(element, element.getTextRange());
    }

    // TODO improve
    @Override
    protected List<PsiElement> resolveInner(boolean incompleteCode) {
        String referenceText = element.getText();
        return findElementsWithinScope(element,
                elem -> elem instanceof FregeFunctionName && elem.getText().equals(referenceText));
    }
}
