package com.plugin.frege.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.psi.FregeDecl;
import com.plugin.frege.psi.FregeFunctionName;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static com.plugin.frege.psi.impl.FregePsiUtilImpl.*;

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
        return declsFromScopeOfElement(element, FregeDecl::getBinding).stream()
                .map(binding -> PsiTreeUtil.findChildOfType(binding, FregeFunctionName.class))
                .filter(keepWithText(referenceText))
                .collect(Collectors.toList());
    }
}
