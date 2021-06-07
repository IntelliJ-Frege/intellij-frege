package com.plugin.frege.documentation

import com.intellij.codeInsight.javadoc.JavaDocUtil
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.plugin.frege.psi.FregeElementProvideDocumentation
import com.plugin.frege.psi.impl.FregeBindingImpl

class FregeDocumentationProvider : AbstractDocumentationProvider() {

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        if (DumbService.isDumb(element.project)) {
            return null
        }
        if (element is FregeBindingImpl) {
            val annoItem = element.getAnnoItem()
            if (annoItem != null) {
                return annoItem.generateDoc()
            }
        }
        return (element as? FregeElementProvideDocumentation)?.generateDoc()
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
