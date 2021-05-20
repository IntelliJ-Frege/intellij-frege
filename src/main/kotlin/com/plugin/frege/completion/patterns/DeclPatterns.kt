package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeLetExpression
import com.plugin.frege.psi.FregeTopDecl
import com.plugin.frege.psi.FregeWhereSection

object DeclPatterns {
    @JvmStatic
    fun declPattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().andOr(
            psiElement().atStartOf(psiElement(FregeTopDecl::class.java)),
            psiElement().afterLeaf(AccessModifierPatterns.accessModifierPattern()),
            psiElement().withParent(FregeLetExpression::class.java),
            psiElement().withParent(FregeWhereSection::class.java)
        )
    }
}