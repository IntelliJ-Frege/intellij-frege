package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.completion.FregeKeywords
import com.plugin.frege.psi.*

object WhereSectionPatterns : PlatformPatterns() {
    @JvmStatic
    fun whereSectionPattern(): PsiElementPattern.Capture<PsiElement> {
        val wherePattern = psiElement().andOr(
            psiElement().withParent(FregeAlt::class.java),
            psiElement().withParent(FregeDataDclConstructors::class.java),
            psiElement().withParent(FregeDataDclNative::class.java),
            psiElement().withParent(FregeClassDcl::class.java),
            psiElement().withParent(FregeInstDcl::class.java),
            psiElement().withParent(FregeRhs::class.java)
        )
        return psiElement().inside(true, wherePattern, psiElement().beforeLeaf(FregeKeywords.WHERE))
    }
}
