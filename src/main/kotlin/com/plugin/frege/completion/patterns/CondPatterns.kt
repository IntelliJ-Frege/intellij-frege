package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement
import com.plugin.frege.completion.FregeKeywords
import com.plugin.frege.psi.FregeCond
import com.plugin.frege.psi.FregeTopEx

object CondPatterns : PlatformPatterns() {
    fun ifPattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().withSuperParent(4, FregeTopEx::class.java)
    }

    fun thenPattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().inside(
            true,
            psiElement(FregeCond::class.java),
            psiElement().afterLeaf(FregeKeywords.THEN)
        )
    }

    fun elsePattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().inside(
            true,
            psiElement(FregeCond::class.java),
            StandardPatterns.or(
                psiElement().afterLeaf(FregeKeywords.IF),
                psiElement().afterLeaf(FregeKeywords.ELSE)
            )
        )
    }
}
