package com.plugin.frege.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiPolyVariantReference
import com.plugin.frege.psi.FregeCompositeElement
import com.plugin.frege.psi.FregeResolvableElement

class FregeUnresolvedReferenceInspection : FregeLocalInspection() {
    override fun visitElement(
        element: FregeCompositeElement,
        holder: ProblemsHolder,
        isOnTheFly: Boolean
    ) {
        if (element !is FregeResolvableElement || element.textLength == 0) {
            return
        }
        val resolves = (element.reference as? PsiPolyVariantReference)?.multiResolve(false)
        if (resolves.isNullOrEmpty()) {
            holder.registerProblem(
                element,
                "Unresolved reference '${element.reference?.canonicalText ?: ""}'",
                ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
                TextRange(0, element.textLength),
                FregeAddImportQuickFix()
            )
        }
    }
}
