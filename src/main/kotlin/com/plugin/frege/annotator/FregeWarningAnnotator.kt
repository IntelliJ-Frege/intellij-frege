package com.plugin.frege.annotator

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeStrongPackage
import com.plugin.frege.intentions.FregePackageToModuleIntentionAction

class FregeWarningAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is FregeStrongPackage) {
            annotateWithWeakWarning(
                element,
                holder,
                "It's not recommended to use the 'package' keyword",
                FregePackageToModuleIntentionAction(element)
            )
        }
    }

    private fun annotateWithWeakWarning(
        element: PsiElement,
        holder: AnnotationHolder,
        message: String,
        intentionAction: IntentionAction?
    ) {
        val builder = holder.newAnnotation(HighlightSeverity.WEAK_WARNING, message)
        if (intentionAction != null) {
            builder.withFix(intentionAction)
        }
        builder.range(element)
        builder.create()
    }
}
