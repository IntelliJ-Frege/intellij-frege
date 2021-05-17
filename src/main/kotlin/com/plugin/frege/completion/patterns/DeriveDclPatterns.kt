package com.plugin.frege.completion.patterns

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeDataDcl
import com.plugin.frege.psi.FregeDeriveDcl
import com.plugin.frege.psi.FregeTopDecl

object DeriveDclPatterns : PlatformPatterns() {
    @JvmStatic
    fun derivePattern(): PsiElementPattern.Capture<PsiElement> {
        return psiElement().andOr(
            psiElement().withParent(FregeTopDecl::class.java),
            psiElement().inside(
                true,
                psiElement(FregeDataDcl::class.java),
                psiElement(FregeDeriveDcl::class.java)
            )
        )
    }
}
