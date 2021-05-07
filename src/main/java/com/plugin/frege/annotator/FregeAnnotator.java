package com.plugin.frege.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.plugin.frege.highlighter.FregeSyntaxHighlighter;
import com.plugin.frege.psi.FregeElementFactory;
import com.plugin.frege.psi.FregeFunctionName;
import com.plugin.frege.psi.FregeStrongKeyword;
import org.jetbrains.annotations.NotNull;

public class FregeAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof FregeFunctionName) {
            annotateFunctionName((FregeFunctionName) element, holder);
        } else if (element instanceof FregeStrongKeyword) {
            annotateStrongKeyword((FregeStrongKeyword) element, holder);
        }
    }

    private void annotateFunctionName(@NotNull FregeFunctionName funcName, @NotNull AnnotationHolder holder) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(funcName.getTextRange())
                .textAttributes(FregeSyntaxHighlighter.FUNCTION_NAME).create();
    }

    private void annotateStrongKeyword(@NotNull FregeStrongKeyword keyword, @NotNull AnnotationHolder holder) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(keyword.getTextRange())
                .textAttributes(FregeSyntaxHighlighter.KEYWORD).create();
    }
}
