package com.plugin.frege.findusages

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.plugin.frege.lexer.layout.FregeLayoutLexerAdapter
import com.plugin.frege.parser.FregeParserDefinition
import com.plugin.frege.psi.*
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls

class FregeFindUsagesProvider : FindUsagesProvider {
    override fun getWordsScanner(): WordsScanner {
        return DefaultWordsScanner(
            FregeLayoutLexerAdapter(),
            FregeParserDefinition.IDENTIFIERS,
            FregeParserDefinition.COMMENTS,
            FregeParserDefinition.STRING_LITERALS,
            TokenSet.EMPTY,
            FregeParserDefinition.OPERATORS
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
            is FregeAnnotationItem -> "annotation"
            is FregeBinding -> {
                when (element.nameIdentifier) {
                    is FregeSymbolOperator -> "operator binding"
                    else -> "function binding"
                }
            }
            is FregeTypedVarid -> "type parameter"
            is FregeDataDecl -> "data"
            is FregeConstruct -> "data constructor"
            is FregeNewtypeDecl -> "newtype"
            is FregeTypeDecl -> "type alias"
            is FregeNativeDataDecl -> "native data"
            is FregeNativeFunction -> "native function"
            is FregeClassDecl -> "class"
            is FregeProgram -> "module"
            is FregeParameter -> "parameter"
            is FregeLabel -> "label"
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