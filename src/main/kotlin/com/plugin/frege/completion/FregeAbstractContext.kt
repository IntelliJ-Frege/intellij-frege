package com.plugin.frege.completion

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiFile

abstract class FregeAbstractContext(idSuffix: String, presentableName: String) :
    TemplateContextType("FREGE_$idSuffix", presentableName, Generic::class.java) {
    class Generic : TemplateContextType("FREGE", "Frege") {
        override fun isInContext(file: PsiFile, offset: Int): Boolean = true
    }
}
