package com.plugin.frege.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.psi.FregeDecl;
import com.plugin.frege.psi.FregeFunctionName;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.plugin.frege.psi.impl.FregePsiUtilImpl.*;

public class FregeQVaridReference extends FregeReferenceBase {
    public FregeQVaridReference(@NotNull PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
    }

    // TODO take into account: qualified names and parameters of bindings.
    @Override
    protected List<PsiElement> resolveInner(boolean incompleteCode) {
        String referenceText = element.getText();

        // check if this expression has `where` ans search there for definitions if it does.
        PsiElement whereDecls = findWhereDeclsInExpression(element);
        if (whereDecls != null) {
            List<PsiElement> whereFuncNames = declsFromScopeOfElement(whereDecls, FregeDecl::getBinding).stream()
                    .map(binding -> PsiTreeUtil.findChildOfType(binding.getLhs(), FregeFunctionName.class))
                    .filter(keepWithText(referenceText))
                    .collect(Collectors.toList());

            if (!whereFuncNames.isEmpty()) {
                return whereFuncNames;
            }
        }

        // searching for definitions in the current and outer scopes
        PsiElement scope = scopeOfElement(element);
        while (scope != null) {
            List<PsiElement> functionNames = declsFromScopeOfElement(scope, FregeDecl::getBinding).stream()
                    .map(binding -> PsiTreeUtil.findChildOfType(binding.getLhs(), FregeFunctionName.class))
                    .filter(Objects::nonNull)
                    .filter(keepWithText(referenceText))
                    .collect(Collectors.toList());

            if (!functionNames.isEmpty()) {
                return functionNames;
            }

            scope = scopeOfElement(scope.getParent());
        }

        return List.of();
    }
}
