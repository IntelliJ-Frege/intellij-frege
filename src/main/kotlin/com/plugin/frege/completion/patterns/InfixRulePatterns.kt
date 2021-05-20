package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeTopDecl

object InfixRulePatterns : PlatformPatterns() {
    @JvmStatic
    fun infixRulePattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().atStartOf(psiElement(FregeTopDecl::class.java))
    }
}
