package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeTopDecl

object ClassDclPatterns {
    @JvmStatic
    fun classOrInterfacePattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().withParent(FregeTopDecl::class.java)
    }
}
