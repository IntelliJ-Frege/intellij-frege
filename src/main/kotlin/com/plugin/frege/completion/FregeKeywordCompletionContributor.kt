package com.plugin.frege.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.ElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.completion.patterns.CaseExpressionPatterns
import com.plugin.frege.completion.patterns.CondPatterns
import com.plugin.frege.completion.patterns.LetExpressionPatterns
import com.plugin.frege.completion.patterns.LetInExpressionPatterns

class FregeKeywordCompletionContributor : CompletionContributor() {
    private fun registerStandardCompletion(pattern: ElementPattern<out PsiElement?>, vararg keywords: String) {
        extend(
            CompletionType.BASIC,
            pattern,
            FregeKeywordCompletionProvider(listOf(*keywords), true)
        )
    }

    init {
        registerStandardCompletion(CaseExpressionPatterns.casePattern(), FregeKeywords.CASE)
        registerStandardCompletion(CaseExpressionPatterns.ofPattern(), FregeKeywords.OF)
        registerStandardCompletion(CondPatterns.ifPattern(), FregeKeywords.IF)
        registerStandardCompletion(CondPatterns.thenPattern(), FregeKeywords.THEN)
        registerStandardCompletion(CondPatterns.elsePattern(), FregeKeywords.ELSE)
        registerStandardCompletion(LetExpressionPatterns.letPattern(), FregeKeywords.LET)
        registerStandardCompletion(LetInExpressionPatterns.inPattern(), FregeKeywords.IN)
    }
}
