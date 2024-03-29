package com.plugin.frege.documentation

import com.intellij.codeInsight.javadoc.JavaDocUtil
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.plugin.frege.psi.FregeDocumentableElement
import com.plugin.frege.psi.impl.FregeBindingImpl

class FregeDocumentationProvider : AbstractDocumentationProvider() {
    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        if (DumbService.isDumb(element.project)) {
            return null
        }
        if (element is FregeBindingImpl) {
            element.getAnnoItem()?.let { return it.generateDoc() }
        }
        return (element as? FregeDocumentableElement)?.generateDoc()
    }

    override fun getDocumentationElementForLink(
        manager: PsiManager?,
        link: String?,
        context: PsiElement?
    ): PsiElement? {
        if (manager == null || link == null) {
            return null
        }
        return JavaDocUtil.findReferenceTarget(manager, link, context, false)
    }
}
