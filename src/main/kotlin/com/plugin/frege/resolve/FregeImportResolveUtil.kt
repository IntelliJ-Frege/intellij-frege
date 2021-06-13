package com.plugin.frege.resolve

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.*
import com.plugin.frege.psi.impl.FregePsiUtilImpl.getQualifiedNameFromUsage
import com.plugin.frege.psi.impl.FregePsiUtilImpl.isElementTypeWithinChildren
import com.plugin.frege.psi.impl.FregePsiUtilImpl.nameFromQualifiedName
import com.plugin.frege.psi.impl.FregePsiUtilImpl.qualifierFromQualifiedName
import com.plugin.frege.psi.mixin.FregeProgramUtil.imports
import com.plugin.frege.stubs.index.FregeShortClassNameIndex

object FregeImportResolveUtil {
    @JvmStatic
    fun findAvailableModulesInImports(imports: List<FregeImportDecl>): List<FregeProgram> {
        val visitedWithoutPublic = HashSet<FregeProgram>()
        val visitedWithPublic = HashSet<FregeProgram>()
        findAvailableModulesInImports(imports, visitedWithoutPublic, visitedWithPublic, true)
        return (visitedWithoutPublic + visitedWithPublic).toList()
    }

    @JvmStatic
    fun findAvailableModulesInImportsForElement(
        project: Project,
        element: PsiElement
    ): List<FregeProgram> {
        val module = element.parentOfType<FregeProgram>() ?: return emptyList()
        val imports = module.imports + getPreludeImport(project)
        return findAvailableModulesInImports(imports) - module
    }

    private fun findAvailableModulesInImports(
        imports: List<FregeImportDecl>,
        visitedWithoutPublic: MutableSet<FregeProgram>,
        visitedWithPublic: MutableSet<FregeProgram>,
        // workaround, because in the start file we don't need `public` to go inside imports
        isStartPoint: Boolean
    ) {
        for (import in imports) {
            val module = getModuleByImport(import) ?: continue
            val isPublic = isElementTypeWithinChildren(import, FregeTypes.PUBLIC_MODIFIER)
            if (!isPublic && !isStartPoint) {
                visitedWithoutPublic.add(module)
                continue
            }
            if (visitedWithPublic.add(module)) {
                val newImports = module.imports
                findAvailableModulesInImports(newImports, visitedWithoutPublic, visitedWithPublic, false)
            }
        }
    }

    private fun getPreludeImport(project: Project): FregeImportDecl {
        return FregeElementFactory.createImportDeclByPackage(project, "frege.Prelude")
    }

    @JvmStatic
    fun getModuleByImport(import: FregeImportDecl): FregeProgram? {
        return import.importPackageName?.importPackageClassName?.reference?.resolve() as? FregeProgram
    }

    @JvmStatic
    fun findClassesFromUsageInImports(usage: PsiElement): List<FregePsiClass> { // TODO incomplete code
        val module = usage.parentOfType<FregeProgram>() ?: return emptyList()
        val project = usage.project
        val qualifiedName = getQualifiedNameFromUsage(usage)
        val qualifier = qualifierFromQualifiedName(qualifiedName).let { it.ifEmpty { null } }
        val name = nameFromQualifiedName(qualifiedName)
        val imports = module.imports + getPreludeImport(project)
        return findClassesByNameInImports(name, qualifier, module, imports)
    }

    @JvmStatic
    fun findClassesByNameInImports(
        name: String,
        qualifier: String?,
        module: FregeProgram,
        imports: List<FregeImportDecl>
    ): List<FregePsiClass> {
        val project = module.project
        val possibleResults = PossibleClassResults.getPossibleResultsForName(name, project)
        val results = mutableListOf<FregePsiClass>()
        val visited = HashSet<Triple<String, String?, FregeProgram>>()
        findClassesByNameInImportsImpl(
            name, qualifier, imports, possibleResults,
            results, visited, true
        )
        return results.distinct().filter { it !== module && it.containingClass !== module } // TODO without workaround
    }

    private fun findClassesByNameInImportsImpl(
        name: String,
        qualifier: String?,
        imports: List<FregeImportDecl>,
        possibleClassResults: PossibleClassResults,
        results: MutableList<FregePsiClass>,
        visited: MutableSet<Triple<String, String?, FregeProgram>>,
        isStartPoint: Boolean
    ) {
        fun visitModule(name: String, qualifier: String?, module: FregeProgram) {
            if (!visited.add(Triple(name, qualifier, module))) {
                return
            }
            if (possibleClassResults.classes.contains(module)) {
                results.add(module)
            } else {
                val clazz = possibleClassResults.moduleToClass[module]
                if (clazz != null) {
                    results.add(clazz)
                }
            }
            findClassesByNameInImportsImpl(
                name, qualifier, module.imports,
                possibleClassResults, results, visited, false
            )
        }

        for (import in imports) {
            val importList = import.importList
            val isImportPublic = isElementTypeWithinChildren(import, FregeTypes.PUBLIC_MODIFIER)
            val isHiding = importList?.strongKeyword?.firstChild?.elementType === FregeTypes.HIDING
            val alias = import.conidUsage // TODO maybe introduce a rule for alias
            val module = getModuleByImport(import) ?: continue
            val moduleName = if (alias != null) alias.text else module.name
            if (moduleName == qualifier) {
                visitModule(name, null, module)
            } else if (qualifier == null) {
                val importSpecs = importList?.importSpecList
                var foundItem = false
                if (importSpecs != null) {
                    for (importSpec in importSpecs) {
                        val isSpecPublic = isElementTypeWithinChildren(importSpec, FregeTypes.PUBLIC_MODIFIER)
                        if (!isStartPoint && !isImportPublic && !isSpecPublic) {
                            continue
                        }
                        val importItem = importSpec.importItem
                        val currentSpecName = importItem.conidUsage?.text
                        if (importItem.importMembers == null && currentSpecName == name) {
                            foundItem = true
                            break
                        }
                    }
                }
                if ((!isHiding && foundItem) || (!foundItem && (importList == null || isHiding))) {
                    visitModule(name, null, module)
                }
            }
        }
    }

    private data class PossibleClassResults(
        val classes: Set<FregePsiClass>,
        val moduleToClass: Map<FregeProgram, FregePsiClass>
    ) {
        companion object {
            fun getPossibleResultsForName(name: String, project: Project): PossibleClassResults {
                val possibleClassesList = FregeShortClassNameIndex.INSTANCE.findByName(
                    name, project, GlobalSearchScope.everythingScope(project)
                ).filter { it.canBeReferenced() }

                val possibleClasses = possibleClassesList.toSet()
                val possibleContainingModules = possibleClassesList.mapNotNull {
                    val containingClass = it.containingClass as? FregeProgram
                    if (containingClass != null) Pair(containingClass, it) else null
                }.toMap()

                return PossibleClassResults(possibleClasses, possibleContainingModules)
            }
        }
    }
}
