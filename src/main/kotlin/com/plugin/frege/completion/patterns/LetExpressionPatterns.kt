package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeTopEx

object LetExpressionPatterns {
    @JvmStatic
    fun letPattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().atStartOf(psiElement(FregeTopEx::class.java))
    }
}
