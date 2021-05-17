package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeTerm

object BooleanLiteralPatterns : PlatformPatterns() {
    @JvmStatic
    fun booleanLiteralPattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().withSuperParent(2, FregeTerm::class.java)
    }

    // TODO support pTerm
}
