package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeTopDecl

object InstDclPatterns : PlatformPatterns() {
    @JvmStatic
    fun instancePattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().andOr(
            psiElement().atStartOf(psiElement(FregeTopDecl::class.java)),
            psiElement().afterLeaf(AccessModifierPatterns.accessModifierPattern())
        )
    }
}
