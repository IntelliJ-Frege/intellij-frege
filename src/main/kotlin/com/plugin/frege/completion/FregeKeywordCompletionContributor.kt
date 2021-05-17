package com.plugin.frege.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.ElementPattern
import com.intellij.psi.PsiElement
import com.plugin.frege.completion.patterns.*

class FregeKeywordCompletionContributor : CompletionContributor() {
    private fun registerStandardCompletion(pattern: ElementPattern<out PsiElement?>, vararg keywords: String) {
        extend(
            CompletionType.BASIC,
            pattern,
            FregeKeywordCompletionProvider(listOf(*keywords), true)
        )
    }

    init {
        registerStandardCompletion(
            AccessModifierPatterns.accessModifierPattern(),
            FregeKeywords.PUBLIC_MODIFIER, FregeKeywords.PROTECTED_MODIFIER, FregeKeywords.PRIVATE_MODIFIER
        )
        registerStandardCompletion(
            BooleanLiteralPatterns.booleanLiteralPattern(),
            FregeKeywords.FALSE, FregeKeywords.TRUE
        )
        registerStandardCompletion(CaseExpressionPatterns.casePattern(), FregeKeywords.CASE)
        registerStandardCompletion(CaseExpressionPatterns.ofPattern(), FregeKeywords.OF)
        registerStandardCompletion(
            ClassDclPatterns.classOrInterfacePattern(),
            FregeKeywords.CLASS, FregeKeywords.INTERFACE
        )
        registerStandardCompletion(CondPatterns.ifPattern(), FregeKeywords.IF)
        registerStandardCompletion(CondPatterns.thenPattern(), FregeKeywords.THEN)
        registerStandardCompletion(CondPatterns.elsePattern(), FregeKeywords.ELSE)
        registerStandardCompletion(DataDclConstructorsPatterns.abstractPattern(), FregeKeywords.ABSTRACT)
        registerStandardCompletion(DataDclConstructorsPatterns.dataPattern(), FregeKeywords.DATA)
        registerStandardCompletion(DataDclNativePatterns.dataPattern(), FregeKeywords.DATA)
        registerStandardCompletion(DataDclNativePatterns.nativePattern(), FregeKeywords.NATIVE)
        registerStandardCompletion(DeriveDclPatterns.derivePattern(), FregeKeywords.DERIVE)
        registerStandardCompletion(DoExpressionPatterns.doExpressionPattern(), FregeKeywords.DO)
        registerStandardCompletion(ImportDclPatterns.importPattern(), FregeKeywords.IMPORT)
        registerStandardCompletion(
            InfixRulePatterns.infixRulePattern(),
            FregeKeywords.INFIX, FregeKeywords.INFIXL, FregeKeywords.INFIXR
        )
        registerStandardCompletion(InstDclPatterns.instancePattern(), FregeKeywords.INSTANCE)
        registerStandardCompletion(LetExpressionPatterns.letPattern(), FregeKeywords.LET)
        registerStandardCompletion(LetInExpressionPatterns.inPattern(), FregeKeywords.IN)
        registerStandardCompletion(NativeFunPatterns.purePattern(), FregeKeywords.PURE)
        registerStandardCompletion(NativeFunPatterns.nativePattern(), FregeKeywords.NATIVE)
        registerStandardCompletion(WhereSectionPatterns.whereSectionPattern(), FregeKeywords.WHERE)
    }
}
