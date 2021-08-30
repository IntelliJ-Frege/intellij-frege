package com.plugin.frege.resolve

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.intellij.util.containers.addIfNotNull
import com.plugin.frege.psi.*
import com.plugin.frege.psi.util.FregeName.Companion.isQualified
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.psi.impl.FregePsiMethodImpl
import com.plugin.frege.psi.util.FregePsiUtil.isElementAccessibleFromModule
import com.plugin.frege.psi.util.FregePsiUtil.isElementTypeWithinChildren
import com.plugin.frege.psi.mixin.FregeProgramUtil.imports
import com.plugin.frege.psi.util.FregeName
import com.plugin.frege.stubs.index.FregeMethodNameIndex
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

    private fun getPreludeImport(project: Project): FregeImportDecl =
        FregeElementFactory.createImportDeclByPackage(project, "frege.Prelude")

    @JvmStatic
    fun getModuleByImport(import: FregeImportDecl): FregeProgram? =
        import.importPackageName?.importPackageClassName?.reference?.resolve() as? FregeProgram

    @JvmStatic
    fun findClassesFromUsageInImports(usage: PsiElement): List<FregePsiClass> {
        val module = usage.parentOfType<FregeProgram>() ?: return emptyList()
        val project = usage.project
        val imports = module.imports + getPreludeImport(project)
        return findClassesByNameInImports(FregeName(usage), module, imports)
    }

    @JvmStatic
    fun findClassesByNameInImports(
        name: FregeName,
        module: FregeProgram,
        imports: List<FregeImportDecl>
    ): List<FregePsiClass> {
        val project = module.project
        val possibleResults = PossibleClassResults.getPossibleResultsForName(name, project)
        val results = mutableListOf<FregePsiClass>()
        val visited = HashSet<FregeProgram>()
        visitImports(imports, object : ImportsProcessor<FregePsiClass>() {
            private fun FregeName.removeQualifierIfMatches(match: String?): FregeName {
                return if (firstQualifier != null) {
                    if (match == firstQualifier) FregeName(null, secondQualifier, shortName) else this
                } else if (secondQualifier != null) {
                    if (match == secondQualifier) FregeName(null, null, shortName) else this
                } else {
                    this
                }
            }

            override fun processModule(module: FregeProgram, hidden: Set<FregePsiClass>, alias: String?): Boolean {
                val actualName = name.removeQualifierIfMatches(alias)
                if (actualName.isQualified || !visited.add(module)) {
                    return false
                }
                val clazz = possibleResults.moduleToClass[module]
                if (clazz != null && clazz !in hidden) {
                    results += clazz
                }
                return true
            }

            override fun processModuleName(moduleOrAlias: FregePsiClass, hidden: Set<FregePsiClass>) {
                if (moduleOrAlias in possibleResults.classes && moduleOrAlias !in hidden) {
                    results += moduleOrAlias
                }
            }

            override fun processImportSpec(importSpec: FregeImportSpec, alias: String?) {
                val actualName = name.removeQualifierIfMatches(alias)
                if (actualName.isQualified) {
                    return
                }
                if (importSpec.importAlias != null) {
                    return // TODO
                }
                val conid = importSpec.importItem.conidUsageImport
                if (conid != null && importSpec.importItem.importMembers == null && conid.text == name.shortName) {
                    results.addIfNotNull(conid.reference?.resolve() as? FregePsiClass)
                }
            }

            override fun hideElements(importItem: FregeImportItem): List<FregePsiClass> {
                val conid = importItem.conidUsageImport
                val hidden = if (conid != null && importItem.importMembers == null) {
                    conid.reference?.resolve() as? FregePsiClass
                } else {
                    null
                }
                return if (hidden != null) listOf(hidden) else emptyList()
            }
        })

        return results
            .distinct()
            .filter { it is FregePsiClassImpl<*> && isElementAccessibleFromModule(it, module) }
    }

    @JvmStatic
    fun findMethodsFromUsageInImports(usage: PsiElement): List<FregePsiMethod> {
        val module = usage.parentOfType<FregeProgram>() ?: return emptyList()
        val project = usage.project
        val imports = module.imports + getPreludeImport(project)
        return findMethodsByNameInImports(FregeName(usage), module, imports)
    }

    @JvmStatic
    fun findMethodsByNameInImports(
        name: FregeName,
        module: FregeProgram,
        imports: List<FregeImportDecl>
    ): List<FregePsiMethod> {
        val project = module.project
        val possibleMethodResults = PossibleMethodResults.getPossibleResultsForMethodName(name, project)
        val results = mutableListOf<FregePsiMethod>()
        val visited = HashSet<FregeProgram>()
        visitImports(imports, object : ImportsProcessor<FregePsiMethod>() {
            private fun FregeName.removeFirstQualifierIfMatches(match: String?): FregeName =
                if (firstQualifier == match) FregeName(null, secondQualifier, shortName) else this

            private fun FregeName.removeSecondQualifierIfMatches(match: String?): FregeName =
                if (firstQualifier == null && secondQualifier == match) FregeName(null, null, shortName) else this

            private fun getMethodFromImportItem(importItem: FregeImportItem): PsiElement? {
                return importItem.qVaridUsageImport?.varidUsageImport
                    ?: importItem.varidUsageImport
                    ?: importItem.symbolOperatorImport
            }

            override fun processModule(module: FregeProgram, hidden: Set<FregePsiMethod>, alias: String?): Boolean {
                val firstQualified = name.removeFirstQualifierIfMatches(alias).firstQualifier != null
                if (firstQualified || !visited.add(module)) {
                    return false
                }
                val secondQualified = name.removeSecondQualifierIfMatches(alias).secondQualifier != null
                val methodsFromModule = possibleMethodResults.moduleToMethods[module]
                if (methodsFromModule != null) {
                    results += if (!secondQualified) {
                        methodsFromModule.filter { it !in hidden }
                    } else {
                        methodsFromModule.filter {
                            it.containingClass != module
                                    && it.containingClass?.name == name.secondQualifier
                                    && it !in hidden
                        }
                    }
                }
                return true
            }

            override fun processImportSpec(importSpec: FregeImportSpec, alias: String?) {
                val firstQualified = name.removeFirstQualifierIfMatches(alias).firstQualifier != null
                if (firstQualified) {
                    return
                }
                if (importSpec.importAlias != null) {
                    return // TODO
                }
                val actualSecondQualifier = name.removeSecondQualifierIfMatches(alias).secondQualifier
                val varid = getMethodFromImportItem(importSpec.importItem) // TODO support importMembers
                if (varid?.text != name.shortName) {
                    return
                }
                val resolved = varid.reference?.resolve() as? FregePsiMethod
                val clazz = resolved?.containingClass
                if (clazz?.name == actualSecondQualifier && clazz !is FregeProgram || actualSecondQualifier == null) {
                    results.addIfNotNull(resolved)
                }
            }

            override fun hideElements(importItem: FregeImportItem): List<FregePsiMethod> {
                val resolved = getMethodFromImportItem(importItem)?.reference?.resolve() as? FregePsiMethod
                return if (resolved != null) listOf(resolved) else emptyList()
            }
        })
        return results.asSequence()
            .distinct()
            .filter { (!it.onlyQualifiedSearch() || name.secondQualifier != null) }
            .filter { it.containingClass != module && it.containingClass?.containingClass != module } // TODO
            .filter { it is FregePsiMethodImpl && isElementAccessibleFromModule(it, module) }
            .toList()
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
        val moduleAlias = import.importDeclAlias?.name ?: import.importPackageName?.importPackageClassName?.text
        val currentAlias = if (isStartPoint) moduleAlias else alias
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

        val module = getModuleByImport(import)
        if ((isHiding || importList == null) && (isImportPublic || isStartPoint)) {
            if (module != null) {
                val shouldVisit = processor.processModule(module, hidden, currentAlias)
                if (shouldVisit) {
                    visitImports(module.imports, processor, false, currentAlias)
                }
            }
        }

        (import.importDeclAlias ?: module)?.let {
            processor.processModuleName(it, hidden)
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
         * Processes [moduleOrAlias] in import statement, considering that [hidden] cannot be resolved.
         * If there is no a module alias for this one, it will be [moduleOrAlias].
         * Otherwise, it's an import alias.
         */
        open fun processModuleName(moduleOrAlias: FregePsiClass, hidden: Set<E>) = Unit

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
            fun getPossibleResultsForName(name: FregeName, project: Project): PossibleClassResults {
                val possibleClassesList = FregeShortClassNameIndex.findByName(
                    name.shortName, project, GlobalSearchScope.everythingScope(project)
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

    private data class PossibleMethodResults(
        val moduleToMethods: Map<FregeProgram, List<FregePsiMethod>>
    ) {
        companion object {
            fun getPossibleResultsForMethodName(name: FregeName, project: Project): PossibleMethodResults {
                val methods = FregeMethodNameIndex.findByName(
                    name.shortName, project, GlobalSearchScope.everythingScope(project)
                )
                val moduleToMethods = methods.mapNotNull {
                    val containingClass = it.containingClass
                    if (containingClass is FregeProgram) {
                        Pair(containingClass, it)
                    } else {
                        val program = containingClass?.containingClass
                        if (program is FregeProgram) {
                            Pair(program, it)
                        } else {
                            null
                        }
                    }
                }.groupBy { it.first }.mapValues { (_, methods) -> methods.map { it.second } }

                return PossibleMethodResults(moduleToMethods)
            }
        }
    }
}
