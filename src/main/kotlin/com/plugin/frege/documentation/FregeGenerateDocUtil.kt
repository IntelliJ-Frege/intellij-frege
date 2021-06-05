package com.plugin.frege.documentation

import com.intellij.lang.documentation.DocumentationMarkup.*
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.FregeDocumentationElement
import com.plugin.frege.psi.FregeProgram
import com.plugin.frege.psi.FregePsiMethod
import com.plugin.frege.psi.impl.FregeAnnotationItemImpl
import com.plugin.frege.psi.impl.FregeBindingImpl
import com.plugin.frege.psi.impl.FregePsiUtilImpl

object FregeGenerateDocUtil {
    private fun renderModuleLinkDocFor(element: PsiElement): String {
        val packageName = element.parentOfType<FregeProgram>()?.packageName ?: return "No module\n"
        return FregeHtmlPsiUtil.packageNameLink(packageName) + "\n"
    }

    private fun renderAnnoItem(annoItem: FregeAnnotationItemImpl?): String {
        val type = annoItem?.getAnnotation()?.sigma?.text ?: return ""
        return "Annotation: $type\n"
    }

    fun generateFregeMethodDoc(method: FregePsiMethod): String {
        val annoItem = when (method) {
            is FregeBindingImpl -> method.getAnnoItem() as? FregeAnnotationItemImpl
            is FregeAnnotationItemImpl -> method
            else -> null
        }
        return buildString {
            append(DEFINITION_START)
            append(renderModuleLinkDocFor(method))
            append(renderAnnoItem(annoItem))
            append(DEFINITION_END)
            append(CONTENT_START)
            append(renderDocs(annoItem?.getDocs()))
            append("<h3>Implementations:</h3>")
            for (binding in FregePsiUtilImpl.getAllBindingsOfMethod(method)) {
                append("<p>")
                append(renderDocs(binding.getDocs()))
                append("<h2>" + binding.lhs.text + "</h2>")
                append("</p>")
            }
            append(CONTENT_END)
        }
    }

    private fun renderDocs(docs: List<FregeDocumentationElement>?): String {
        return docs?.joinToString("\n") { doc -> doc.documentationText } ?: ""
    }
}