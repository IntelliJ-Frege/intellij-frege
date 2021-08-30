package com.plugin.frege.completion

import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.plugin.frege.FregeFileType

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
            get() = FregePatterns.ConditionPatterns.ifPattern()
    }

    class FregeClassDecl : FregePatternedContext("CLASSDECL", "Class declaration") {
        override val pattern: PsiElementPattern.Capture<PsiElement>
            get() = FregePatterns.ClassDeclPatterns.classOrInterfacePattern()
    }

    class FregeInstanceDecl : FregePatternedContext("INSTANCEDECL", "Instance declaration") {
        override val pattern: PsiElementPattern.Capture<PsiElement>
            get() = FregePatterns.InstanceDeclPatterns.instancePattern()
    }

    class FregeDecl : FregePatternedContext("DECL", "Declaration") {
        override val pattern: PsiElementPattern.Capture<PsiElement>
            get() = FregePatterns.DeclPatterns.declPattern()
    }
}
