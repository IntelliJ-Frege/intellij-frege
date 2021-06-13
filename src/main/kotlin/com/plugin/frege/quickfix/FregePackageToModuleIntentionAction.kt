package com.plugin.frege.quickfix

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.plugin.frege.psi.FregeElementFactory

class FregePackageToModuleIntentionAction(private val myPackage: PsiElement) : BaseIntentionAction() {
    init {
        text = "Replace with 'module'"
    }

    override fun getFamilyName(): String {
        return "Package to module conversion"
    }

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        return myPackage.isValid && myPackage.isWritable
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        myPackage.replace(FregeElementFactory.createModuleKeyword(project))
    }
}
