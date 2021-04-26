package com.plugin.frege.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.psi.FregeBody;
import com.plugin.frege.psi.FregeFunctionName;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FregeFunctionNameReference extends FregeReferenceBase {
    public FregeFunctionNameReference(@NotNull PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
    }

    // TODO take into account scopes
    @Override
    protected List<PsiElement> resolveInner(boolean incompleteCode) {
        FregeBody body = PsiTreeUtil.getParentOfType(element, FregeBody.class);
        if (body == null) {
            return List.of();
        }
        return PsiTreeUtil.findChildrenOfType(body, FregeFunctionName.class).stream()
                .filter(Objects::nonNull)
                .filter(elem -> Objects.equals(elem.getText(), element.getText()))
                .collect(Collectors.toList());
    }
}
