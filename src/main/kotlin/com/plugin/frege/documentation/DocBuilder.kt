package com.plugin.frege.documentation

import com.intellij.codeInsight.documentation.DocumentationManagerUtil
import com.intellij.lang.documentation.DocumentationMarkup.*
import com.plugin.frege.psi.FregeElementProvideDocumentation
import com.plugin.frege.psi.FregeProgram
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.FregePsiMethod

class DocBuilder {

    private val builder = StringBuilder()

    private fun psiElementLink(fqn: String?, label: String?): DocBuilder {
        if (fqn != null && label != null) {
            DocumentationManagerUtil.createHyperlink(builder, fqn, label, false)
        }
        return this
    }

    fun moduleLink(fregeProgram: FregeProgram?): DocBuilder {
        val moduleName = fregeProgram?.packageName?.text
        return psiElementLink(moduleName, moduleName)
    }

    fun psiMethodLink(method: FregePsiMethod?): DocBuilder {
        return psiElementLink(method?.containingClass?.qualifiedName + "#" + method?.name, method?.name)
    }

    fun psiClassLink(psiClass: FregePsiClass?): DocBuilder {
        return psiElementLink(psiClass?.qualifiedName, psiClass?.name)
    }

    fun appendText(string: String?): DocBuilder {
        if (string != null) {
            builder.append(string)
        }
        return this
    }

    fun appendCode(string: String?): DocBuilder {
        if (string != null) {
            builder.append("<code>")
            builder.append(string)
            builder.append("</code>")
        }
        return this
    }

    fun definition(builderAction: DocBuilder.() -> Unit): DocBuilder {
        appendText(DEFINITION_START)
        apply(builderAction)
        appendText(DEFINITION_END)
        return this
    }

    fun content(builderAction: DocBuilder.() -> Unit): DocBuilder {
        appendText(CONTENT_START)
        apply(builderAction)
        appendText(CONTENT_END)
        return this
    }

    fun paragraph(builderAction: DocBuilder.() -> Unit): DocBuilder {
        appendText("<p>")
        apply(builderAction)
        appendText("</p>")
        return this
    }

    fun appendDocs(element: FregeElementProvideDocumentation?): DocBuilder {
        if (element != null) {
            appendText(element.getDocs().joinToString("<br>") { doc -> doc.documentationText.trim() })
        }
        return this
    }

    fun section(title: String, builderAction: DocBuilder.() -> Unit): DocBuilder {
        appendText("<h3>$title</h3>")
        apply(builderAction)
        return this
    }

    fun appendNewline(): DocBuilder {
        appendText("<br>")
        return this
    }

    override fun toString(): String {
        return builder.toString()
    }
}

inline fun buildDoc(builderAction: DocBuilder.() -> Unit): String {
    return DocBuilder().apply(builderAction).toString()
}
