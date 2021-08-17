package com.plugin.frege.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.plugin.frege.highlighter.FregeSyntaxHighlighter
import com.plugin.frege.psi.*
import com.plugin.frege.psi.util.FregePsiUtil

class FregeAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is FregeFunctionName || element is FregeNativeFunctionName || element is FregeAnnotationName) {
            annotateWithInfo(element, holder, FregeSyntaxHighlighter.FUNCTION_NAME)
        } else if (element is FregeStrongKeyword) {
            annotateWithInfo(element, holder, FregeSyntaxHighlighter.KEYWORD)
        } else if (element is FregePackageName || element is FregeImportPackageName) {
            annotateWithInfo(element, holder, FregeSyntaxHighlighter.TYPE)
        } else if (element is FregeTypeParameter) {
            annotateWithInfo(element, holder, FregeSyntaxHighlighter.TYPE_PARAMETER)
        } else if (FregePsiUtil.isLeaf(element) && element.text == undefinedIdentifier) {
            annotateWithInfo(element, holder, FregeSyntaxHighlighter.UNDEFINED)
        }
    }

    private fun annotateWithInfo(
        element: PsiElement,
        holder: AnnotationHolder,
        attributesKey: TextAttributesKey
    ) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element.textRange)
            .textAttributes(attributesKey).create()
    }

    companion object {
        private const val undefinedIdentifier = "undefined"
    }
}
