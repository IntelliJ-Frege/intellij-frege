package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeTopDecl

object ImportDclPatterns : PlatformPatterns() {
    @JvmStatic
    fun importPattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().atStartOf(psiElement(FregeTopDecl::class.java))
    }

    // TODO as and public modifier patterns
}
