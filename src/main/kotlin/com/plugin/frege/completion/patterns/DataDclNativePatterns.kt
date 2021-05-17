package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeTopDecl

object DataDclNativePatterns : PlatformPatterns() {
    @JvmStatic
    fun dataPattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().withParent(FregeTopDecl::class.java)
    }

    @JvmStatic
    fun nativePattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().withParent(FregeTopDecl::class.java)
    }
}
