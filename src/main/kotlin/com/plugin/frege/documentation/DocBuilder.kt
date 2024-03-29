package com.plugin.frege.documentation

import com.intellij.codeInsight.documentation.DocumentationManagerUtil
import com.intellij.lang.documentation.DocumentationMarkup.*
import com.plugin.frege.psi.FregeDocumentationElement
import com.plugin.frege.psi.FregeProgram
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.FregePsiMethod

class DocBuilder {
    private val builder = StringBuilder()

    private fun appendPsiElementLink(fqn: String?, label: String?): DocBuilder {
        if (fqn != null && label != null) {
            DocumentationManagerUtil.createHyperlink(builder, fqn, label, false)
        }
        return this
    }

    fun appendModuleLink(fregeProgram: FregeProgram?): DocBuilder {
        val moduleName = fregeProgram?.qualifiedName
        return appendPsiElementLink(moduleName, moduleName)
    }

    fun appendPsiMethodLink(method: FregePsiMethod?): DocBuilder {
        return appendPsiElementLink(method?.containingClass?.qualifiedName + "#" + method?.name, method?.name)
    }

    fun appendPsiClassLink(psiClass: FregePsiClass?): DocBuilder {
        return appendPsiElementLink(psiClass?.qualifiedName, psiClass?.qualifiedName)
    }

    fun appendText(string: String): DocBuilder {
        builder.append(string)
        return this
    }

    fun appendBoldText(string: String): DocBuilder {
        builder.append("<b>")
        builder.append(string)
        builder.append("</b>")
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

    fun appendNewline(): DocBuilder {
        return appendText("<br>")
    }

    fun appendDocs(docComments: List<FregeDocumentationElement>): DocBuilder {
        return appendText(docComments.joinToString("<br>") { doc ->
            FregeDocUtil.escapeDocText(
                doc.getDocumentationText().trim()
            )
        })
    }

    fun paragraph(builderAction: DocBuilder.() -> Unit): DocBuilder {
        appendText("<p>")
        apply(builderAction)
        appendText("</p>")
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

    fun section(title: String, builderAction: DocBuilder.() -> Unit): DocBuilder {
        builder.append("<h3 style=\"margin-top:0;margin-bottom:0\">$title</h3>")
        apply(builderAction)
        return this
    }

    override fun toString(): String {
        return builder.toString()
    }
}

inline fun buildDoc(builderAction: DocBuilder.() -> Unit): String {
    return DocBuilder().apply(builderAction).toString()
}
