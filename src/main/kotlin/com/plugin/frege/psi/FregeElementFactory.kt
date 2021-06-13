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
        return createElementOrNull(project, text) ?: cannotCreateElement()
    }

    private inline fun <reified E> cannotCreateElement(): E {
        throw IllegalStateException("Cannot create an element ${E::class}")
    }

    @JvmStatic
    fun createModuleKeyword(project: Project): FregeStrongModule {
        val fakeModule = "module $fakeModuleName where\nfunc = undefined"
        return createElement(project, fakeModule)
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
    fun createVaridUsageOrNull(project: Project, name: String): FregeVaridUsage? {
        val fakeVaridUsage = fakeProgram + "function = " + name
        return createElementOrNull(project, fakeVaridUsage)
    }

    @JvmStatic
    fun createVaridUsage(project: Project, name: String): FregeVaridUsage {
        return createVaridUsageOrNull(project, name) ?: cannotCreateElement()
    }

    @JvmStatic
    fun canCreateVaridUsage(project: Project, name: String): Boolean {
        return createVaridUsageOrNull(project, name) != null
    }

    private fun createConidUsageOrNull(project: Project, name: String): FregeConidUsage? {
        val fakeConidUsage = "module $name where\nfunc = undefined"
        return createElementOrNull(project, fakeConidUsage)
    }

    @JvmStatic
    fun createConidUsage(project: Project, name: String): FregeConidUsage {
        return createConidUsageOrNull(project, name) ?: cannotCreateElement()
    }

    @JvmStatic
    fun canCreateConidUsage(project: Project, name: String): Boolean {
        return createConidUsageOrNull(project, name) != null
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

    private fun createSymbolOperatorOrNull(project: Project, name: String): FregeSymbolOperator? {
        val fakeSymbolOperator = "${fakeProgram}($name) :: Int -> Int -> Int"
        return createElementOrNull(project, fakeSymbolOperator)
    }

    @JvmStatic
    fun createSymbolOperator(project: Project, name: String): FregeSymbolOperator {
        return createSymbolOperatorOrNull(project, name) ?: cannotCreateElement()
    }

    @JvmStatic
    fun canCreateSymbolOperator(project: Project, name: String): Boolean {
        return createSymbolOperatorOrNull(project, name) != null
    }

    @JvmStatic
    fun createTypeParameter(project: Project, name: String): FregeTypeParameter {
        val fakeTypeParameter = "${fakeProgram}func :: $name"
        return createElement(project, fakeTypeParameter)
    }

    @JvmStatic
    fun createLabelName(project: Project, name: String): FregeLabelName {
        val fakeLabel = "${fakeProgram}data Hello a = Hello { $name :: a }"
        return createElement(project, fakeLabel)
    }

    @JvmStatic
    fun createImportPackageClassName(project: Project, name: String): FregeImportPackageClassName {
        val fakePackageClassName = "${fakeProgram}import hello.$name"
        return createElement(project, fakePackageClassName)
    }

    @JvmStatic
    fun createImportDeclByPackage(project: Project, name: String): FregeImportDecl {
        val fakeImportDecl = "${fakeProgram}import $name"
        return createElement(project, fakeImportDecl)
    }

    @JvmStatic
    fun createImportDecl(project: Project, import: String): FregeImportDecl {
        val fakeImportDecl = "${fakeProgram}$import"
        return createElement(project, fakeImportDecl)
    }
}
