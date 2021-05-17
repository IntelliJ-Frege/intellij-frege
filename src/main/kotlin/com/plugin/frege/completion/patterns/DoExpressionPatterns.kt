package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeTopEx

object DoExpressionPatterns : PlatformPatterns() {
    @JvmStatic
    fun doExpressionPattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().withParent(FregeTopEx::class.java)
    }
}
