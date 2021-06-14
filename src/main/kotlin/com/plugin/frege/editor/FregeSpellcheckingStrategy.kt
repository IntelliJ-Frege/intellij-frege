package com.plugin.frege.editor

import com.intellij.psi.PsiElement
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.plugin.frege.FregeLanguage

class FregeSpellcheckingStrategy : SpellcheckingStrategy() {
    override fun isMyContext(element: PsiElement): Boolean {
        return element.language is FregeLanguage
    }
}
