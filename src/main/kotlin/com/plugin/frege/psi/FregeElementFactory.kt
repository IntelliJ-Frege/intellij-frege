package com.plugin.frege.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.FregeLanguage

object FregeElementFactory {
    private const val fakeModuleName = "Dummy"
    private const val fakeFileName = "$fakeModuleName.fr"
    private const val fakeProgram = "module $fakeModuleName where\n"

    private fun createFile(project: Project, text: String): FregeFile {
        return PsiFileFactory.getInstance(project)
            .createFileFromText(fakeFileName, FregeLanguage.INSTANCE, text) as FregeFile
    }

    private inline fun <reified E : PsiElement> createElementOrNull(
        project: Project,
        text: String,
    ): E? {
        val file = createFile(project, text)
        return PsiTreeUtil.findChildrenOfType(file, E::class.java).lastOrNull()
    }

    private inline fun <reified E : PsiElement> createElement(
        project: Project,
        text: String,
    ): E {
        return createElementOrNull(project, text)
            ?: throw IllegalArgumentException("Cannot create an element in the factory.")
    }

    @JvmStatic
    fun createFunctionName(project: Project, name: String): FregeFunctionName {
        val fakeFunction = "$fakeProgram$name = undefined"
        return createElement(project, fakeFunction)
    }

    @JvmStatic
    fun createParam(project: Project, name: String): FregeParameter {
        val fakeParam = fakeProgram + "function " + name + " = undefined"
        return createElement(project, fakeParam)
    }

    @JvmStatic
    fun createVarId(project: Project, name: String): FregeQVarid {
        val fakeVarId = fakeProgram + "function = " + name
        return createElement(project, fakeVarId)
    }

    @JvmStatic
    fun createConidUsage(project: Project, name: String): FregeConidUsage {
        val fakeConidUsage = "${fakeProgram}func :: $name"
        return createElement(project, fakeConidUsage)
    }

    @JvmStatic
    fun createAnnotationName(project: Project, name: String): FregeAnnotationName {
        val fakeAnnotationName = "${fakeProgram}$name :: Int"
        return createElement(project, fakeAnnotationName)
    }

    @JvmStatic
    fun createPackageToken(project: Project, name: String): FregePackageToken {
        val fakePackageToken = "${fakeProgram}import $name.Hello\nmain = undefined"
        return createElement(project, fakePackageToken)
    }

    @JvmStatic
    fun createNativeFunctionName(project: Project, name: String): FregeNativeFunctionName {
        val fakeNativeFunctionName = "${fakeProgram}native $name :: Int -> Int"
        return createElement(project, fakeNativeFunctionName)
    }

    @JvmStatic
    fun createSymbolOperator(project: Project, name: String): FregeSymbolOperator {
        val fakeSymbolOperator = "${fakeProgram}($name) :: Int -> Int -> Int"
        return createElement(project, fakeSymbolOperator)
    }

    @JvmStatic
    fun canCreateSymbolOperator(project: Project, name: String): Boolean {
        val fakeSymbolOperator = "${fakeProgram}($name) :: Int -> Int -> Int"
        return createElementOrNull<FregeSymbolOperator>(project, fakeSymbolOperator) != null
    }
}
