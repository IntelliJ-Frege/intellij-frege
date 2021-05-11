package com.plugin.frege.resolve;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.FregeDataNameNative;
import com.plugin.frege.psi.FregeElementFactory;
import com.plugin.frege.psi.impl.FregePsiClassUtilImpl;
import com.plugin.frege.psi.impl.FregePsiUtilImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.plugin.frege.psi.impl.FregePsiUtilImpl.findAvailableDataDecls;
import static com.plugin.frege.psi.impl.FregePsiUtilImpl.keepWithText;

public class FregeDataNameUsageReference extends FregeReferenceBase {
    public FregeDataNameUsageReference(@NotNull PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
    }

    @Override
    protected List<PsiElement> resolveInner(boolean incompleteCode) {
        List<PsiElement> currentFileData = tryFindDataInCurrentFile();
        if (!currentFileData.isEmpty()) {
            return currentFileData;
        }

        return tryFindDataByImports();
    }

    @Override
    public PsiElement handleElementRename(@NotNull String name) throws IncorrectOperationException {
        return element.replace(FregeElementFactory.createDataNameUsage(element.getProject(), name));
    }

    private List<PsiElement> tryFindDataInCurrentFile() {
        String referenceText = element.getText();
        return findAvailableDataDecls(element).stream()
                .map(decl -> PsiTreeUtil.findChildOfType(decl, FregeDataNameNative.class))
                .map(Objects::requireNonNull)
                .filter(keepWithText(referenceText))
                .collect(Collectors.toList());
    }

    private List<PsiElement> tryFindDataByImports() {
        String clazzName = element.getText();
        Project project = element.getProject();

        List<String> imports = FregePsiUtilImpl.findImportsNamesForElement(element, true);
        for (String currentImport : imports) {
            String qualifiedName = currentImport + "." + clazzName;
            List<PsiClass> classes = FregePsiClassUtilImpl.getClassesByQualifiedName(project, qualifiedName);
            if (!classes.isEmpty()) {
                return new ArrayList<>(classes);
            }
        }

        return List.of();
    }
}
