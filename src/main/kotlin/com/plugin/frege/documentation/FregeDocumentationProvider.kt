package com.plugin.frege.documentation

import com.intellij.codeInsight.javadoc.JavaDocUtil
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.plugin.frege.psi.FregePsiMethod
import com.plugin.frege.psi.impl.FregeClassDeclImpl
import com.plugin.frege.documentation.FregeGenerateDocUtil as Doc

class FregeDocumentationProvider : AbstractDocumentationProvider() {

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        if (DumbService.isDumb(element.project)) {
            return null
        }
        return when (element) {
            is FregePsiMethod -> Doc.generateFregeMethodDoc(element)
            is FregeClassDeclImpl -> Doc.generateFregeClassDoc(element)
            else -> null
        }
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
