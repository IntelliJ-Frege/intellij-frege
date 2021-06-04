package com.plugin.frege.refactor

import com.intellij.patterns.ElementPattern
import com.intellij.patterns.ElementPatternCondition
import com.intellij.patterns.InitialPatternCondition
import com.intellij.psi.PsiElement
import com.intellij.refactoring.rename.RenameInputValidator
import com.intellij.util.ProcessingContext
import com.plugin.frege.psi.FregeElementFactory
import com.plugin.frege.psi.FregeNamedElement
import com.plugin.frege.psi.FregeSymbolOperator

class FregeSymbolOperatorRenameInputValidator : RenameInputValidator {
    private val pattern = object : ElementPattern<PsiElement> {
        private val condition =
            ElementPatternCondition(object : InitialPatternCondition<PsiElement>(PsiElement::class.java) {
                override fun accepts(o: Any?, context: ProcessingContext?) = false
            })

        override fun accepts(o: Any?): Boolean {
            return o is FregeNamedElement && o.nameIdentifier is FregeSymbolOperator
        }

        override fun accepts(o: Any?, context: ProcessingContext?): Boolean {
            return accepts(o)
        }

        override fun getCondition(): ElementPatternCondition<PsiElement> {
            return condition
        }
    }

    override fun getPattern(): ElementPattern<out PsiElement> {
        return pattern
    }

    override fun isInputValid(newName: String, element: PsiElement, context: ProcessingContext): Boolean {
        return FregeElementFactory.canCreateSymbolOperator(element.project, newName)
    }
}
