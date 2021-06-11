package com.plugin.frege.resolve

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.FregeElementFactory
import com.plugin.frege.psi.FregeImportDecl
import com.plugin.frege.psi.FregeProgram
import com.plugin.frege.psi.FregeTypes
import com.plugin.frege.psi.impl.FregePsiUtilImpl

object FregeImportResolveUtil {
    @JvmStatic
    fun findAvailableModulesInImports(
        project: Project,
        imports: List<FregeImportDecl>
    ): List<FregeProgram> {
        val visitedWithoutPublic = HashSet<FregeProgram>()
        val visitedWithPublic = HashSet<FregeProgram>()
        val importsWithPrelude = imports + getPreludeImport(project)
        findAvailableModulesInImports(importsWithPrelude, visitedWithoutPublic, visitedWithPublic, true)
        return (visitedWithoutPublic + visitedWithPublic).toList()
    }

    @JvmStatic
    fun findAvailableModulesInImportsForElement(
        project: Project,
        element: PsiElement
    ): List<FregeProgram> {
        val program = element.parentOfType<FregeProgram>() ?: return emptyList()
        val imports = findImportsInModule(program)
        return findAvailableModulesInImports(project, imports) - program
    }

    private fun findAvailableModulesInImports(
        imports: List<FregeImportDecl>,
        visitedWithoutPublic: MutableSet<FregeProgram>,
        visitedWithPublic: MutableSet<FregeProgram>,
        // workaround, because in the start file we don't need `public` to go inside imports
        isStartPoint: Boolean
    ) {
        for (import in imports) {
            val module = import.importPackageName?.importPackageClassName?.reference?.resolve()
            if (module !is FregeProgram) {
                continue
            }
            val isPublic = FregePsiUtilImpl.isElementTypeWithinChildren(import, FregeTypes.PUBLIC_MODIFIER)
            if (!isPublic && !isStartPoint) {
                visitedWithoutPublic.add(module)
                continue
            }
            if (visitedWithPublic.add(module)) {
                val newImports = findImportsInModule(module)
                findAvailableModulesInImports(newImports, visitedWithoutPublic, visitedWithPublic, false)
            }
        }
    }

    private fun getPreludeImport(project: Project): FregeImportDecl {
        return FregeElementFactory.createImportDecl(project, "frege.Prelude")
    }

    @JvmStatic
    fun findImportsInModule(module: FregeProgram): List<FregeImportDecl> {
        return module.body?.topDeclList?.mapNotNull { it.firstChild as? FregeImportDecl } ?: emptyList()
    }
}
