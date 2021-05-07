package com.plugin.frege.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.plugin.frege.highlighter.FregeSyntaxHighlighter;
import com.plugin.frege.psi.FregeFunctionName;
import com.plugin.frege.psi.FregeStrongKeyword;
import com.plugin.frege.psi.impl.FregePsiUtilImpl;
import org.jetbrains.annotations.NotNull;

public class FregeAnnotator implements Annotator {

    private static final String undefinedIdentifier = "undefined";

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof FregeFunctionName) {
            annotateFunctionName((FregeFunctionName) element, holder);
        } else if (element instanceof FregeStrongKeyword) {
            annotateStrongKeyword((FregeStrongKeyword) element, holder);
        } else if (FregePsiUtilImpl.isLeaf(element) && element.getText().equals(undefinedIdentifier)) {
            annotateUndefined(element, holder);
        }
    }

    private void annotateFunctionName(@NotNull FregeFunctionName funcName, @NotNull AnnotationHolder holder) {
        annotateWithInfo(funcName, holder, FregeSyntaxHighlighter.FUNCTION_NAME);
    }

    private void annotateStrongKeyword(@NotNull FregeStrongKeyword keyword, @NotNull AnnotationHolder holder) {
        annotateWithInfo(keyword, holder, FregeSyntaxHighlighter.KEYWORD);
    }

    private void annotateUndefined(@NotNull PsiElement undefined, @NotNull AnnotationHolder holder) {
        annotateWithInfo(undefined, holder, FregeSyntaxHighlighter.UNDEFINED);
    }

    private void annotateWithInfo(@NotNull PsiElement element, @NotNull AnnotationHolder holder,
                                  @NotNull TextAttributesKey attributesKey) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(element.getTextRange())
                .textAttributes(attributesKey).create();
    }
}
