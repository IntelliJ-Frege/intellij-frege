package com.plugin.frege.documentation

import com.intellij.lang.documentation.DocumentationMarkup.*
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.FregeClassDecl
import com.plugin.frege.psi.FregeDocumentationElement
import com.plugin.frege.psi.FregeProgram
import com.plugin.frege.psi.FregePsiMethod
import com.plugin.frege.psi.impl.FregeAnnotationItemImpl
import com.plugin.frege.psi.impl.FregeBindingImpl
import com.plugin.frege.psi.impl.FregeClassDeclImpl
import com.plugin.frege.psi.impl.FregePsiUtilImpl

object FregeGenerateDocUtil {
    @JvmStatic
    fun generateFregeMethodDoc(method: FregePsiMethod): String {
        val annoItem = when (method) {
            is FregeBindingImpl -> method.getAnnoItem() as? FregeAnnotationItemImpl
            is FregeAnnotationItemImpl -> method
            else -> null
        }
        return buildString {
            append(DEFINITION_START)
            append(renderModuleLinkDocFor(method))
            append("<h3>Function ${method.name}</h3>")
            append(renderAnnotationIn(annoItem))
            append(renderWithin(method))
            append(DEFINITION_END)
            append(CONTENT_START)
            append(renderDocs(annoItem?.getDocs()))
            append("<h3>Implementations:</h3>")
            for (binding in FregePsiUtilImpl.getAllBindingsOfMethod(method)) {
                append("<p>")
                append(renderDocs(binding.getDocs()))
                append("<h4><code>" + binding.lhs.text + "</code></h4>")
                append("</p>")
            }
            append(CONTENT_END)
        }
    }

    @JvmStatic
    fun generateFregeClassDoc(fregeClass: FregeClassDeclImpl): String {
        val uniqueMethods = fregeClass.allMethods.distinctBy { it.name }.mapNotNull { it as? FregePsiMethod }
        return buildString {
            append(DEFINITION_START)
            append(renderModuleLinkDocFor(fregeClass))
            append("<h3>Class ${fregeClass.name}</h3>")
            append(DEFINITION_END)
            append(CONTENT_START)
            append(renderDocs(fregeClass.getDocs()))
            append("<h3>Functions:</h3>")
            for (method in uniqueMethods) {
                append("<p>")
                append(FregeHtmlPsiUtil.psiMethodLink(method))
                append("</p>")
            }
            append(CONTENT_END)
        }
    }

    @JvmStatic
    private fun renderModuleLinkDocFor(element: PsiElement): String {
        val packageName = element.parentOfType<FregeProgram>()?.packageName ?: return "No module"
        return FregeHtmlPsiUtil.packageNameLink(packageName)
    }

    @JvmStatic
    private fun renderAnnotationIn(annoItem: FregeAnnotationItemImpl?): String {
        val type = annoItem?.getAnnotation()?.sigma?.text ?: return ""
        return "<h3><i>$type</i></h3>"
    }

    @JvmStatic
    private fun renderWithin(method: FregePsiMethod): String {
        val classDecl = method.containingClass as? FregeClassDecl ?: return ""
        val classLink = FregeHtmlPsiUtil.psiClassLink(classDecl) ?: return ""
        return " within $classLink"
    }

    @JvmStatic
    private fun renderDocs(docs: List<FregeDocumentationElement>?): String {
        return docs?.joinToString("<br>") { doc -> doc.documentationText.trim() } ?: ""
    }
}
