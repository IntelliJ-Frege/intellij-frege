package com.plugin.frege.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.plugin.frege.highlighter.FregeSyntaxHighlighter;
import com.plugin.frege.psi.*;
import com.plugin.frege.psi.util.FregePsiUtil;
import org.jetbrains.annotations.NotNull;

public class FregeAnnotator implements Annotator {

    private static final String undefinedIdentifier = "undefined";

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof FregeFunctionName || element instanceof FregeNativeFunctionName
                || element instanceof FregeAnnotationName) {
            annotateWithInfo(element, holder, FregeSyntaxHighlighter.FUNCTION_NAME);
        } else if (element instanceof FregeStrongKeyword) {
            annotateWithInfo(element, holder, FregeSyntaxHighlighter.KEYWORD);
        } else if (element instanceof FregePackageName || element instanceof FregeImportPackageName) {
            annotateWithInfo(element, holder, FregeSyntaxHighlighter.TYPE);
        } else if (element instanceof FregeTypeParameter) {
            annotateWithInfo(element, holder, FregeSyntaxHighlighter.TYPE_PARAMETER);
        } else if (FregePsiUtil.isLeaf(element) && element.getText().equals(undefinedIdentifier)) {
            annotateWithInfo(element, holder, FregeSyntaxHighlighter.UNDEFINED);
        }
    }

    private void annotateWithInfo(@NotNull PsiElement element, @NotNull AnnotationHolder holder,
                                  @NotNull TextAttributesKey attributesKey) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(element.getTextRange())
                .textAttributes(attributesKey).create();
    }
}
