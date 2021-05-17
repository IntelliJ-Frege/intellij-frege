package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.completion.FregeKeywords.IN
import com.plugin.frege.completion.FregeKeywords.LET
import com.plugin.frege.psi.FregeLetInExpression

object LetInExpressionPatterns {
    @JvmStatic
    fun inPattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().inside(
            true,
            psiElement(FregeLetInExpression::class.java).afterLeaf(LET),
            psiElement().afterLeaf(IN)
        )
    }
}
