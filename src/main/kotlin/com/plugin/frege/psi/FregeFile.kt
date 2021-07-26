package com.plugin.frege.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.refactoring.rename.RenameProcessor
import com.plugin.frege.FregeFileType
import com.plugin.frege.FregeLanguage

class FregeFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, FregeLanguage.INSTANCE) {
    override fun getFileType(): FileType {
        return FregeFileType.INSTANCE
    }

    override fun setName(name: String): PsiElement {
        val module = firstChild as? FregeProgram
        val fileName = nameFromFile(this.name)
        if (module?.name == fileName) {
            RenameProcessor(
                module.project,
                module,
                nameFromFile(name),
                false,
                false
            ).run()
        }
        return super.setName(name)
    }

    override fun toString(): String {
        return "Frege File"
    }

    private fun nameFromFile(name: String): String = name.substringBeforeLast(".")
}
