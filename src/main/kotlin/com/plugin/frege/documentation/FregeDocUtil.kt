package com.plugin.frege.documentation

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.psi.FregeDocumentationElement
import com.plugin.frege.psi.FregeLinearIndentSectionItemsSemicolon
import com.plugin.frege.psi.FregeLinearIndentSectionItemsVirtual
import com.plugin.frege.psi.impl.FregePsiUtilImpl

object FregeDocUtil {

    @JvmStatic
    fun collectDocComments(element: PsiElement): List<FregeDocumentationElement> {
        return collectPrecedingDocs(element) + PsiTreeUtil.getChildrenOfTypeAsList(
            element,
            FregeDocumentationElement::class.java
        )
    }

    @JvmStatic
    private fun collectPrecedingDocs(element: PsiElement): List<FregeDocumentationElement> {
        val parentInScope =
            PsiTreeUtil.findFirstParent(element) {
                FregePsiUtilImpl.isScope(it.parent) ||
                        it.parent is FregeLinearIndentSectionItemsVirtual ||
                        it.parent is FregeLinearIndentSectionItemsSemicolon
            } ?: return emptyList()
        return FregePsiUtilImpl.siblingBackwardSequenceSkippingWhitespacesAndComments(parentInScope, true)
            .takeWhile { it is FregeDocumentationElement || FregePsiUtilImpl.isEndDeclElement(it) }
            .mapNotNull { it as? FregeDocumentationElement }.toList().asReversed()
    }

}
