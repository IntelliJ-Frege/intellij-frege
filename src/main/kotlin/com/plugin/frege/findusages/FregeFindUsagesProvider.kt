package com.plugin.frege.findusages

import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.plugin.frege.lexer.FregeLexerAdapter
import com.plugin.frege.parser.FregeParserDefinition
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.*
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls

class FregeFindUsagesProvider : FindUsagesProvider {
    override fun getWordsScanner(): WordsScanner { // TODO maybe we should implement words scanner manually
        return DefaultWordsScanner(
            FregeLexerAdapter(),
            FregeParserDefinition.IDENTIFIERS,
            FregeParserDefinition.COMMENTS,
            FregeParserDefinition.STRING_LITERALS
        )
    }

    override fun canFindUsagesFor(element: PsiElement): Boolean {
        return element is FregeNamedElement
    }

    @NonNls
    override fun getHelpId(element: PsiElement): String? {
        return null
    }

    override fun getType(element: PsiElement): @Nls String {
        return when (element) {
            is FregeAnnotation -> "annotation"
            is FregeBinding -> "function binding"
            is FregeDataDclNative -> "native data"
            is FregeClassDcl -> "class"
            is FregePackageClassName -> "module"
            is FregeParam -> "parameter"
            else -> "" // TODO
        }
    }

    override fun getDescriptiveName(element: PsiElement): @Nls String {
        return when (element) {
            is FregeNamedElement -> element.name ?: element.text
            is FregeFile -> element.name
            else -> ""
        }
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): @Nls String {
        return when (element) {
            is FregeNamedElement -> element.name ?: element.text
            else -> element.text
        }
    }
}