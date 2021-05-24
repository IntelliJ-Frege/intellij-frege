package com.plugin.frege.findusages

import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.*
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls

class FregeFindUsagesProvider : FindUsagesProvider {
    override fun getWordsScanner(): WordsScanner {
        return FregeWordsScanner()
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
            is FregeNativeDataDecl -> "native data"
            is FregeClassDecl -> "class"
            is FregeProgram -> "module"
            is FregeParameter -> "parameter"
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