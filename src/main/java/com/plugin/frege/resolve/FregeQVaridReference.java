package com.plugin.frege.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FregeQVaridReference extends FregeReferenceBase {
    public FregeQVaridReference(@NotNull PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
    }

    // TODO take into account scopes and qualified names
    @Override
    protected List<PsiElement> resolveInner(boolean incompleteCode) {
        FregeBody body = PsiTreeUtil.getParentOfType(element, FregeBody.class);
        if (body == null) {
            return List.of();
        }
        return PsiTreeUtil.findChildrenOfType(body, FregeFunctionName.class).stream()
                .filter(Objects::nonNull)
                .filter(funcName -> Objects.equals(funcName.getName(), element.getText()))
                .collect(Collectors.toList());
    }
}
