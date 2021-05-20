package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.completion.FregeKeywords
import com.plugin.frege.psi.FregeTopDecl

object DataDclConstructorsPatterns : PlatformPatterns() {
    @JvmStatic
    fun abstractPattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().andOr(
            psiElement().atStartOf(psiElement(FregeTopDecl::class.java)),
            psiElement().afterLeaf(AccessModifierPatterns.accessModifierPattern())
        )
    }

    @JvmStatic
    fun dataPattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().withParent(
            psiElement().afterLeaf(FregeKeywords.ABSTRACT)
        )
    }
}
