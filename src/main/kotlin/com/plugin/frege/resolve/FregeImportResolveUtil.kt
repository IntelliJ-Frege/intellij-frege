package com.plugin.frege.resolve

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.intellij.util.containers.addIfNotNull
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
        visitImports(imports, object : ImportsProcessor<FregePsiClass>() {
            private fun actualQualifier(alias: String?): String? {
                return if (qualifier == alias) null else qualifier
            }

            override fun processModule(module: FregeProgram, hidden: Set<FregePsiClass>, alias: String?): Boolean {
                if (module in possibleResults.classes && module !in hidden) {
                    results += module
                }
                val actualQualifier = actualQualifier(alias)
                if (!visited.add(Triple(name, qualifier, module))) {
                    return false
                }
                if (actualQualifier == null) {
                    val clazz = possibleResults.moduleToClass[module]
                    if (clazz != null) {
                        results += clazz
                    }
                }
                return true
            }

            override fun processImportSpec(importSpec: FregeImportSpec, alias: String?) {
                val actualQualifier = actualQualifier(alias)
                if (actualQualifier != null) {
                    return
                }
                if (importSpec.importAlias != null) {
                    return // TODO
                }
                val conid = importSpec.importItem.conidUsageFromImportList
                if (conid != null && importSpec.importItem.importMembers == null && conid.text == name) {
                    results.addIfNotNull(conid.reference?.resolve() as? FregePsiClass)
                }
            }

            override fun hideElements(importItem: FregeImportItem): List<FregePsiClass> {
                val conid = importItem.conidUsageFromImportList
                val hidden = if (conid != null && importItem.importMembers == null) {
                    conid.reference?.resolve() as? FregePsiClass
                } else {
                    null
                }
                return if (hidden != null) listOf(hidden) else emptyList()
            }
        })

        return results.distinct().filter { it !== module && it.containingClass !== module } // TODO without workaround
    }

    private fun <E : PsiElement> visitImports(
        imports: List<FregeImportDecl>,
        processor: ImportsProcessor<E>,
        isStartPoint: Boolean = true,
        alias: String? = null
    ) {
        for (import in imports) {
            visitImport(import, processor, isStartPoint, alias)
        }
    }

    private fun <E : PsiElement> visitImport(
        import: FregeImportDecl,
        processor: ImportsProcessor<E>,
        isStartPoint: Boolean,
        alias: String?
    ) {
        val importList = import.importList
        val isImportPublic = isElementTypeWithinChildren(import, FregeTypes.PUBLIC_MODIFIER)
        val isHiding = importList?.strongKeyword?.firstChild?.elementType === FregeTypes.HIDING
        val currentAlias = if (isStartPoint) import.importDeclAlias?.name else alias
        val hidden = HashSet<E>()
        val importSpecs = importList?.importSpecList
        if (importSpecs != null) {
            for (importSpec in importSpecs) {
                val isSpecPublic = isElementTypeWithinChildren(importSpec, FregeTypes.PUBLIC_MODIFIER)
                if (isHiding) {
                    hidden += processor.hideElements(importSpec.importItem)
                } else if (isStartPoint || isImportPublic || isSpecPublic) {
                    processor.processImportSpec(importSpec, currentAlias)
                }
            }
        }

        if ((isHiding || importList == null) && (isImportPublic || isStartPoint)) {
            val module = getModuleByImport(import)
            if (module != null) {
                val shouldVisit = processor.processModule(module, hidden, currentAlias)
                if (shouldVisit) {
                    visitImports(module.imports, processor, false, currentAlias)
                }
            }
        }
    }

    private open class ImportsProcessor<E : PsiElement> {
        /**
         * Processes [module], considering that [hidden] cannot be resolved.
         *
         * [alias] presents the first alias in the import sequence (file -> file -> file).
         * @return if imports in the [module] should be processed as well.
         */
        open fun processModule(module: FregeProgram, hidden: Set<E>, alias: String?): Boolean = false

        /**
         * Processes [importSpec],
         * considering that [alias] is the first alias on the import sequence (file -> file -> file).
         */
        open fun processImportSpec(importSpec: FregeImportSpec, alias: String?) = Unit

        /**
         * @return result of hiding [importItem]. These elements will be added to `hidden` in [processModule].
         */
        open fun hideElements(importItem: FregeImportItem): List<E> = emptyList()
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
