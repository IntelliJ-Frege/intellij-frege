package com.plugin.frege.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.FregeLanguage
import kotlin.reflect.KClass

object FregeElementFactory {
    private const val fakeModuleName = "Dummy"
    private const val fakeFileName = "$fakeModuleName.fr"
    private const val fakeProgram = "module $fakeModuleName where\n"

    private fun createFile(project: Project, text: String): FregeFile {
        return PsiFileFactory.getInstance(project)
            .createFileFromText(fakeFileName, FregeLanguage.INSTANCE, text) as FregeFile
    }

    private fun <E : PsiElement> createElement(project: Project, text: String, elementClass: KClass<E>): E {
        val file = createFile(project, text)
        return PsiTreeUtil.findChildrenOfType(file, elementClass.java).last()
    }

    @JvmStatic
    fun createFunctionName(project: Project, name: String): FregeFunctionName {
        val fakeFunction = "$fakeProgram$name = undefined"
        return createElement(project, fakeFunction, FregeFunctionName::class)
    }

    @JvmStatic
    fun createParam(project: Project, name: String): FregeParam {
        val fakeParam = fakeProgram + "function " + name + " = undefined"
        return createElement(project, fakeParam, FregeParam::class)
    }

    @JvmStatic
    fun createVarId(project: Project, name: String): FregeQVarId {
        val fakeVarId = fakeProgram + "function = " + name
        return createElement(project, fakeVarId, FregeQVarId::class)
    }

    @JvmStatic
    fun createNativeName(project: Project, name: String): FregeNativeName {
        val fakeNativeName = "${fakeProgram}pure native Dummy = $name"
        return createElement(project, fakeNativeName, FregeNativeName::class)
    }

    @JvmStatic
    fun createDataNameUsage(project: Project, name: String): FregeDataNameUsage {
        val fakeDataNameUsage = "${fakeProgram}func :: $name"
        return createElement(project, fakeDataNameUsage, FregeDataNameUsage::class)
    }

    @JvmStatic
    fun createAnnotationName(project: Project, name: String): FregeAnnotationName {
        val fakeAnnotationName = "${fakeProgram}$name :: Int"
        return createElement(project, fakeAnnotationName, FregeAnnotationName::class)
    }
}
