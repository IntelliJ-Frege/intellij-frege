package com.plugin.frege.documentation

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.impl.FregeBindingImpl
import com.plugin.frege.documentation.FregeGenerateDocUtil as Doc

class FregeDocumentationProvider : AbstractDocumentationProvider() {
    override fun getQuickNavigateInfo(element: PsiElement, originalElement: PsiElement): String? {
        return null // TODO
    }

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        if (element is FregeBindingImpl) {
            return Doc.generateFregeMethodDoc(element)
        }
        return null
    }
}