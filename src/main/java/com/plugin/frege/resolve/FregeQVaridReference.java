package com.plugin.frege.resolve;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.FregeBinding;
import com.plugin.frege.psi.FregeElementFactory;
import com.plugin.frege.psi.FregeFunctionName;
import com.plugin.frege.psi.FregeWhereSection;
import com.plugin.frege.psi.impl.FregePsiClassUtilImpl;
import com.plugin.frege.psi.impl.FregePsiUtilImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
        List<PsiElement> result = new ArrayList<>(tryFindFunction(incompleteCode));
        if (!result.isEmpty() && !incompleteCode) {
            return result;
        }

        result.addAll(tryFindParameters(incompleteCode));
        if (!result.isEmpty() && !incompleteCode) {
            return result;
        }

        result.addAll(tryFindInMethodsOfOtherClasses(incompleteCode));

        return result;
    }

    private List<PsiElement> tryFindFunction(boolean incompleteCode) {
        Predicate<PsiElement> predicate = getByTypePredicateCheckingText(FregeFunctionName.class, element, incompleteCode);

        // check if this expression has `where` ans search there for definitions if it does.
        FregeWhereSection where = findWhereInExpression(element);
        if (where != null) {
            List<PsiElement> whereFuncNames = findElementsWithinScope(where.getIndentSection(), predicate);

            if (!whereFuncNames.isEmpty()) {
                return whereFuncNames;
            }
        }

        // searching for definitions in the current and outer scopes
        PsiElement scope = scopeOfElement(element);
        while (scope != null) {
            List<PsiElement> functionNames = findElementsWithinScope(scope, predicate);

            if (!functionNames.isEmpty()) {
                return functionNames;
            }

            scope = scopeOfElement(scope.getParent());
        }

        return List.of();
    }

    private List<PsiElement> tryFindParameters(boolean incompleteCode) {
        Predicate<PsiElement> predicate = getByTypePredicateCheckingText(FregeFunctionName.class, element, incompleteCode);

        FregeWhereSection where = findWhereInExpression(element);
        if (where != null) {
            List<PsiElement> params = findElementsWithinScope(where, predicate);
            if (!params.isEmpty()) {
                return params;
            }
        }

        FregeBinding binding = parentBinding(element);
        while (binding != null) {
            List<PsiElement> params = findElementsWithinElement(binding, predicate);
            if (!params.isEmpty()) {
                return params;
            }

            binding = parentBinding(binding.getParent());
        }

        return List.of();
    }

    private List<PsiElement> tryFindInMethodsOfOtherClasses(boolean incompleteCode) {
        String methodName = element.getText();
        Project project = element.getProject();

        List<String> imports = FregePsiUtilImpl.findImportsNamesForElement(element, true);

        List<PsiElement> result = new ArrayList<>();
        for (String currentImport : imports) {
            if (incompleteCode) {
                result.addAll(FregePsiClassUtilImpl.getAllMethodsByImportName(project, currentImport));
            } else {
                String qualifiedName = FregePsiUtilImpl.mergeQualifiedNames(currentImport, methodName);
                if (qualifiedName == null) {
                    continue;
                }
                result.addAll(FregePsiClassUtilImpl.getMethodsByQualifiedName(project, qualifiedName));
                if (!result.isEmpty()) {
                    break;
                }
            }
        }
        return result;
    }
}
