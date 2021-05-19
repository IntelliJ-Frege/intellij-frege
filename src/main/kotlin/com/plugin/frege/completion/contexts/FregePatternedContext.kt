package com.plugin.frege.completion.contexts

import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.plugin.frege.FregeFileType
import com.plugin.frege.completion.patterns.ClassDclPatterns
import com.plugin.frege.completion.patterns.CondPatterns

abstract class FregePatternedContext(idSuffix: String, presentableName: String) :
    FregeAbstractContext(idSuffix, presentableName) {
    abstract val pattern: PsiElementPattern.Capture<PsiElement>

    override fun isInContext(file: PsiFile, offset: Int): Boolean {
        if (file.fileType != FregeFileType.INSTANCE) {
            return false
        }
        return pattern.accepts(file.findElementAt(offset))
    }

    class FregeIf : FregePatternedContext("IF", "If") {
        override val pattern: PsiElementPattern.Capture<PsiElement>
            get() = CondPatterns.ifPattern()
    }

    class FregeClassDecl : FregePatternedContext("CLASSDECL", "Class declaration") {
        override val pattern: PsiElementPattern.Capture<PsiElement>
            get() = ClassDclPatterns.classOrInterfacePattern()
    }
}