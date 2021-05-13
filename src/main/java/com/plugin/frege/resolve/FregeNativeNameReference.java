package com.plugin.frege.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.plugin.frege.psi.impl.FregePsiClassUtilImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FregeNativeNameReference extends FregeReferenceBase {
    public FregeNativeNameReference(@NotNull PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
    }

    @Override
    protected List<PsiElement> resolveInner(boolean incompleteCode) {
        return new ArrayList<>(FregePsiClassUtilImpl.getClassesByQualifiedName(element.getProject(), element.getText())); // TODO support incomplete code
    }
}
