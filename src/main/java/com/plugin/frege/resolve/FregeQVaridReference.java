package com.plugin.frege.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.plugin.frege.psi.impl.FregePsiUtilImpl.*;

public class FregeQVaridReference extends FregeReferenceBase {
    public FregeQVaridReference(@NotNull PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
    }

    @Override
    public PsiElement handleElementRename(@NotNull String name) throws IncorrectOperationException {
        return element.replace(FregeElementFactory.createVarId(element.getProject(), name));
    }

    // TODO take into account: qualified names
    @Override
    protected List<PsiElement> resolveInner(boolean incompleteCode) {
        List<PsiElement> functions = tryFindFunction();
        if (!functions.isEmpty()) {
            return functions;
        }

        List<PsiElement> params = tryFindParameters();
        if (!params.isEmpty()) {
            return params;
        }

        return List.of();
    }

    private List<PsiElement> tryFindFunction() {
        String referenceText = element.getText();

        // check if this expression has `where` ans search there for definitions if it does.
        FregeWhereSection where = findWhereInExpression(element);
        if (where != null) {
            List<PsiElement> whereFuncNames = findElementsWithinScope(where.getIndentSection(),
                    element -> element instanceof FregeFunctionName && element.getText().equals(referenceText));

            if (!whereFuncNames.isEmpty()) {
                return whereFuncNames;
            }
        }

        // searching for definitions in the current and outer scopes
        PsiElement scope = scopeOfElement(element);
        while (scope != null) {
            List<PsiElement> functionNames = findElementsWithinScope(scope,
                    element -> element instanceof FregeFunctionName && element.getText().equals(referenceText));

            if (!functionNames.isEmpty()) {
                return functionNames;
            }

            scope = scopeOfElement(scope.getParent());
        }

        return List.of();
    }

    private List<PsiElement> tryFindParameters() {
        String referenceText = element.getText();

        FregeBinding binding = parentBinding(element);
        while (binding != null) {
            List<PsiElement> params = findElementsWithinElement(binding,
                    elem -> elem instanceof FregeParam && elem.getText().equals(referenceText));
            if (!params.isEmpty()) {
                return params;
            }

            binding = parentBinding(binding.getParent());
        }

        return List.of();
    }
}
