package com.plugin.frege.documentation

import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.parser.FregeParserDefinition.COMMENTS
import com.plugin.frege.parser.FregeParserDefinition.WHITE_SPACES
import com.plugin.frege.psi.FregeDocumentationElement
import com.plugin.frege.psi.util.FregePsiUtil
import com.plugin.frege.psi.util.FregePsiUtil.isScope
import org.apache.commons.lang.StringEscapeUtils

object FregeDocUtil {
    @JvmStatic
    fun escapeDocText(text: String): String {
        return StringEscapeUtils.escapeHtml(text).replace("\r\n|\n\r|\r|\n".toRegex(), "<br/>")
    }

    @JvmStatic
    fun collectDocComments(element: PsiElement): List<FregeDocumentationElement> {
        return collectPrecedingDocs(element) + PsiTreeUtil.getChildrenOfTypeAsList(
            element,
            FregeDocumentationElement::class.java
        )
    }

    @JvmStatic
    private fun collectPrecedingDocs(element: PsiElement): List<FregeDocumentationElement> {
        val parentInScope = PsiTreeUtil.findFirstParent(element) { isScope(it.parent) } ?: return emptyList()
        val siblingsBackwardSequenceSkipping = FregePsiUtil.siblingsBackwardSequenceSkipping(
            parentInScope,
            true,
            TokenSet.orSet(WHITE_SPACES, COMMENTS)
        )
        return siblingsBackwardSequenceSkipping
            .takeWhile { it is FregeDocumentationElement || FregePsiUtil.isEndDeclElement(it) }
            .filterIsInstance<FregeDocumentationElement>()
            .toList()
            .asReversed()
    }
}
