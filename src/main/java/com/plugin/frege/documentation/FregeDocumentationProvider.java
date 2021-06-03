package com.plugin.frege.documentation;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiElement;
import com.plugin.frege.psi.FregeDocumentationElement;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

public class FregeDocumentationProvider extends AbstractDocumentationProvider {
    @Override
    public @Nullable String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return null; // TODO
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @Nullable @NlsSafe String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (!(element instanceof FregeElementProvideDocumentation)) {
            return null;
        }
        FregeElementProvideDocumentation elementProvideDocumentation = (FregeElementProvideDocumentation) (element);
        return elementProvideDocumentation.getDocs().stream().map(FregeDocumentationElement::getDocumentationText).collect(Collectors.joining("\n"));
    }
}
