package com.plugin.frege.refactor

import com.intellij.patterns.ElementPattern
import com.intellij.patterns.ElementPatternCondition
import com.intellij.patterns.InitialPatternCondition
import com.intellij.psi.PsiElement
import com.intellij.refactoring.rename.RenameInputValidator
import com.intellij.util.ProcessingContext
import com.plugin.frege.psi.*

class FregeRenameInputValidator : RenameInputValidator {
    private val pattern = object : ElementPattern<PsiElement> {
        private val condition =
            ElementPatternCondition(object : InitialPatternCondition<PsiElement>(PsiElement::class.java) {
                override fun accepts(o: Any?, context: ProcessingContext?) = false
            })

        override fun accepts(o: Any?): Boolean = o is FregeNamedElement

        override fun accepts(o: Any?, context: ProcessingContext?): Boolean = accepts(o)

        override fun getCondition(): ElementPatternCondition<PsiElement> = condition
    }

    override fun getPattern(): ElementPattern<out PsiElement> = pattern

    override fun isInputValid(newName: String, element: PsiElement, context: ProcessingContext): Boolean {
        if (newName.contains(' ')) {
            return false
        }
        val project = element.project
        val nameIdentifier = (element as? FregeNamedElement)?.nameIdentifier ?: return false
        return when (nameIdentifier) {
            is FregeSymbolOperator -> FregeElementFactory.canCreateSymbolOperator(project, newName)
            is FregeWordOperator -> FregeElementFactory.canCreateWordOperator(project, newName)
            is FregeConidUsage -> FregeElementFactory.canCreateConidUsage(project, newName)
            else -> FregeElementFactory.canCreateVaridUsage(project, newName)
        }
    }
}
