package com.plugin.frege.completion

import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.psi.PsiElement

object FregeCompletionUtil {
    @JvmStatic
    fun shouldComplete(element: PsiElement): Boolean {
        // There are problems with unnecessary completion after numbers/etc
        // The first condition for optimization
        return element.text == CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED
    }
}
