package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.completion.FregeKeywords
import com.plugin.frege.psi.FregeTopDecl

object DataDclConstructorsPatterns : PlatformPatterns() {
    @JvmStatic
    fun abstractPattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().withParent(FregeTopDecl::class.java)
    }

    @JvmStatic
    fun dataPattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().inside(
            true,
            psiElement().withParent(FregeTopDecl::class.java),
            psiElement().afterLeaf(FregeKeywords.DATA)
        )
    }
}
