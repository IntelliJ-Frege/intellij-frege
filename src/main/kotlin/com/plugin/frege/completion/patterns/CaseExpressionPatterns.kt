package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.completion.FregeKeywords
import com.plugin.frege.psi.FregeCaseEx
import com.plugin.frege.psi.FregeTopEx

object CaseExpressionPatterns : PlatformPatterns() {
    fun casePattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().withSuperParent(4, FregeTopEx::class.java)
    }

    fun ofPattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().inside(true,
            psiElement(FregeCaseEx::class.java),
            psiElement().afterLeaf(FregeKeywords.OF))
    }
}
