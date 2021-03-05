package com.plugin.frege.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.plugin.frege.highlighter.FregeSyntaxHighlighter;
import com.plugin.frege.psi.FregeFunctionName;
import org.jetbrains.annotations.NotNull;

public class FregeAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof FregeFunctionName) {
            annotateFunctionName(element, holder);
        }
        //TODO more smart highlighting (type and etc)
    }

    private void annotateFunctionName(@NotNull PsiElement funcName, @NotNull AnnotationHolder holder) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(funcName.getTextRange())
                .textAttributes(FregeSyntaxHighlighter.FUNCTION_NAME).create();
    }
}
