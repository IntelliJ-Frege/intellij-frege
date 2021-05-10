package com.plugin.frege.resolve;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.*;
import com.plugin.frege.psi.impl.FregePsiClassUtilImpl;
import com.plugin.frege.psi.impl.FregePsiUtilImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.plugin.frege.psi.impl.FregePsiUtilImpl.*;

public class FregeQVaridReference extends FregeReferenceBase {
    private static final List<String> defaultImports = List.of(
            "frege.Prelude"
    );

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

        List<PsiElement> methods = tryFindInMethodsOfOtherClasses();
        if (!methods.isEmpty()) {
            return methods;
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

        FregeWhereSection where = findWhereInExpression(element);
        if (where != null) {
            List<PsiElement> params = findElementsWithinScope(where,
                    elem -> elem instanceof FregeParam && elem.getText().equals(referenceText));
            if (!params.isEmpty()) {
                return params;
            }
        }

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

    private List<PsiElement> tryFindInMethodsOfOtherClasses() {
        String methodName = element.getText();
        Project project = element.getProject();

        List<String> imports = FregePsiUtilImpl.findImportsForElement(element).stream()
                .map(FregeImportDcl::getImportPackageName)
                .filter(Objects::nonNull)
                .map(PsiElement::getText)
                .collect(Collectors.toList());
        imports.addAll(defaultImports);

        for (String currentImport : imports) {
            String qualifiedName = currentImport + "." + methodName;
            List<PsiMethod> methods = FregePsiClassUtilImpl.getMethodsByQualifiedName(project, qualifiedName);
            if (!methods.isEmpty()) {
                return new ArrayList<>(methods);
            }
        }

        return List.of();
    }
}
