package com.plugin.frege.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.plugin.frege.psi.FregeFunctionName;
import com.plugin.frege.psi.FregeWhereSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        FregeWhereSection where = findWhereInExpression(element);
        if (where != null) {
            List<PsiElement> whereFuncNames = findElementsWithinScope(where.getDecls(),
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
}
