package com.plugin.frege.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.ElementPattern
import com.intellij.psi.PsiElement

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
            FregePatterns.AccessModifierPatterns.accessModifierPattern(),
            FregeKeywords.PUBLIC_MODIFIER, FregeKeywords.PROTECTED_MODIFIER, FregeKeywords.PRIVATE_MODIFIER
        )
        registerStandardCompletion(
            FregePatterns.BooleanLiteralPatterns.booleanLiteralTermPattern(),
            FregeKeywords.FALSE, FregeKeywords.TRUE
        )
        registerStandardCompletion(
            FregePatterns.BooleanLiteralPatterns.booleanLiteralPatternTermPattern(),
            FregeKeywords.FALSE, FregeKeywords.TRUE
        )
        registerStandardCompletion(
            FregePatterns.BooleanLiteralPatterns.booleanLiteralTopDeclarationPattern(),
            FregeKeywords.FALSE, FregeKeywords.TRUE
        )
        registerStandardCompletion(FregePatterns.CaseExpressionPatterns.casePattern(), FregeKeywords.CASE)
        registerStandardCompletion(FregePatterns.CaseExpressionPatterns.ofPattern(), FregeKeywords.OF)
        registerStandardCompletion(
            FregePatterns.ClassDeclPatterns.classOrInterfacePattern(),
            FregeKeywords.CLASS, FregeKeywords.INTERFACE
        )
        registerStandardCompletion(FregePatterns.ConditionPatterns.ifPattern(), FregeKeywords.IF)
        registerStandardCompletion(FregePatterns.ConditionPatterns.thenPattern(), FregeKeywords.THEN)
        registerStandardCompletion(FregePatterns.ConditionPatterns.elsePattern(), FregeKeywords.ELSE)
        registerStandardCompletion(FregePatterns.DataDeclConstructorsPatterns.abstractPattern(), FregeKeywords.ABSTRACT)
        registerStandardCompletion(FregePatterns.DataDeclConstructorsPatterns.dataPattern(), FregeKeywords.DATA)
        registerStandardCompletion(FregePatterns.DataDeclNativePatterns.dataPattern(), FregeKeywords.DATA)
        registerStandardCompletion(FregePatterns.DataDeclNativePatterns.nativePattern(), FregeKeywords.NATIVE)
        registerStandardCompletion(FregePatterns.DeriveDeclPatterns.derivePattern(), FregeKeywords.DERIVE)
        registerStandardCompletion(FregePatterns.DoExpressionPatterns.doExpressionPattern(), FregeKeywords.DO)
        registerStandardCompletion(FregePatterns.ImportDeclPatterns.importPattern(), FregeKeywords.IMPORT)
        registerStandardCompletion(
            FregePatterns.InfixRulePatterns.infixRulePattern(),
            FregeKeywords.INFIX, FregeKeywords.INFIXL, FregeKeywords.INFIXR
        )
        registerStandardCompletion(FregePatterns.InstanceDeclPatterns.instancePattern(), FregeKeywords.INSTANCE)
        registerStandardCompletion(FregePatterns.LetExpressionPatterns.letPattern(), FregeKeywords.LET)
        registerStandardCompletion(FregePatterns.LetInExpressionPatterns.inPattern(), FregeKeywords.IN)
        registerStandardCompletion(FregePatterns.NativeFunPatterns.purePattern(), FregeKeywords.PURE)
        registerStandardCompletion(FregePatterns.NativeFunPatterns.nativePattern(), FregeKeywords.NATIVE)
        registerStandardCompletion(FregePatterns.WhereSectionPatterns.whereSectionPattern(), FregeKeywords.WHERE)
    }
}
