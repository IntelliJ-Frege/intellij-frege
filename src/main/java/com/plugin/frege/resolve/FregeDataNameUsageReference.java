package com.plugin.frege.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.FregeDataName;
import com.plugin.frege.psi.FregeElementFactory;
import org.jetbrains.annotations.NotNull;

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
        String referenceText = element.getText();
        return findAvailableDataDecls(element).stream()
                .map(decl -> PsiTreeUtil.findChildOfType(decl, FregeDataName.class))
                .map(Objects::requireNonNull)
                .filter(keepWithText(referenceText))
                .collect(Collectors.toList());
    }

    @Override
    public PsiElement handleElementRename(@NotNull String name) throws IncorrectOperationException {
        return element.replace(FregeElementFactory.createDataNameUsage(element.getProject(), name));
    }
}
