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

    private fun getPreludeImport(project: Project): FregeImportDecl {
        return FregeElementFactory.createImportDeclByPackage(project, "frege.Prelude")
    }

    @JvmStatic
    fun getModuleByImport(import: FregeImportDecl): FregeProgram? {
        return import.importPackageName?.importPackageClassName?.reference?.resolve() as? FregeProgram
    }

    @JvmStatic
    fun findClassesFromUsageInImports(usage: PsiElement): List<FregePsiClass> {
        val module = usage.parentOfType<FregeProgram>() ?: return emptyList()
        val project = usage.project
        val qualifiedName = getQualifiedNameFromUsage(usage)
        val qualifier = qualifierFromQualifiedName(qualifiedName).ifEmpty { null }
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
        val visited = HashSet<FregeProgram>()
        visitImports(imports, object : ImportsProcessor<FregePsiClass>() {
            private fun actualQualifier(alias: String?): String? {
                return if (qualifier == alias) null else qualifier
            }

            override fun processModule(module: FregeProgram, hidden: Set<FregePsiClass>, alias: String?): Boolean {
                val actualQualifier = actualQualifier(alias)
                if (actualQualifier != null || !visited.add(module)) {
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
                val actualQualifier = actualQualifier(alias)
                if (actualQualifier != null) {
                    return
                }
                if (importSpec.importAlias != null) {
                    return // TODO
                }
                val conid = importSpec.importItem.conidUsageImport
                if (conid != null && importSpec.importItem.importMembers == null && conid.text == name) {
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

        return results.distinct()
    }

    @JvmStatic
    fun findMethodsFromUsageInImports(usage: PsiElement): List<FregePsiMethod> {
        val module = usage.parentOfType<FregeProgram>() ?: return emptyList()
        val project = usage.project
        val qualifiedName = getQualifiedNameFromUsage(usage)
        val qualifier = qualifierFromQualifiedName(qualifiedName).ifEmpty { null }
        val firstQualifier: String?
        val secondQualifier: String?
        if (qualifier != null) {
            firstQualifier = qualifierFromQualifiedName(qualifier).ifEmpty { null }
            secondQualifier = nameFromQualifiedName(qualifier)
        } else {
            firstQualifier = null
            secondQualifier = null
        }
        val name = nameFromQualifiedName(qualifiedName)
        val imports = module.imports + getPreludeImport(project)
        return findMethodsByNameInImports(name, firstQualifier, secondQualifier, module, imports)
    }

    @JvmStatic
    fun findMethodsByNameInImports(
        name: String,
        firstQualifier: String?,
        secondQualifier: String?,
        module: FregeProgram,
        imports: List<FregeImportDecl>
    ): List<FregePsiMethod> {
        val project = module.project
        val possibleMethodResults = PossibleMethodResults.getPossibleResultsForMethodName(name, project)
        val results = mutableListOf<FregePsiMethod>()
        val visited = HashSet<FregeProgram>()
        visitImports(imports, object : ImportsProcessor<FregePsiMethod>() {
            private fun actualFirstQualifier(alias: String?): String? {
                return if (firstQualifier == alias) null else firstQualifier
            }

            private fun actualSecondQualifier(alias: String?): String? {
                return if (firstQualifier == null && secondQualifier == alias) null else secondQualifier
            }

            private fun getVaridFromImportItem(importItem: FregeImportItem): FregeVaridUsageImport? {
                return importItem.qVaridUsageImport?.varidUsageImport ?: importItem.varidUsageImport
            }

            override fun processModule(module: FregeProgram, hidden: Set<FregePsiMethod>, alias: String?): Boolean {
                val actualFirstQualifier = actualFirstQualifier(alias)
                if (actualFirstQualifier != null || !visited.add(module)) {
                    return false
                }
                val actualSecondQualifier = actualSecondQualifier(alias)
                val methodsFromModule = possibleMethodResults.moduleToMethods[module]
                if (methodsFromModule != null) {
                    results += if (actualSecondQualifier == null) {
                        methodsFromModule.filter { it !in hidden }
                    } else {
                        methodsFromModule.filter {
                            it.containingClass != module && it.containingClass?.name == secondQualifier
                                    && it !in hidden
                        }
                    }
                }

                return true
            }

            override fun processImportSpec(importSpec: FregeImportSpec, alias: String?) {
                val actualFirstQualifier = actualFirstQualifier(alias)
                if (actualFirstQualifier != null) {
                    return
                }
                if (importSpec.importAlias != null) {
                    return // TODO
                }
                val actualSecondQualifier = actualSecondQualifier(alias)
                val varid = getVaridFromImportItem(importSpec.importItem) // TODO support importMembers
                if (varid?.text != name) {
                    return
                }
                val resolved = varid.reference?.resolve() as? FregePsiMethod
                val clazz = resolved?.containingClass
                if (clazz?.name == actualSecondQualifier && clazz !is FregeProgram || actualSecondQualifier == null) {
                    results.addIfNotNull(resolved)
                }
            }

            override fun hideElements(importItem: FregeImportItem): List<FregePsiMethod> {
                val resolved = getVaridFromImportItem(importItem)?.reference?.resolve() as? FregePsiMethod
                return if (resolved != null) listOf(resolved) else emptyList()
            }
        })
        return results.asSequence()
            .distinct()
            .filter{ (!it.onlyQualifiedSearch() || secondQualifier != null) }
            .filter { it.containingClass != module && it.containingClass?.containingClass != module } // TODO
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

    private data class PossibleMethodResults(
        val moduleToMethods: Map<FregeProgram, List<FregePsiMethod>>
    ) {
        companion object {
            fun getPossibleResultsForMethodName(name: String, project: Project): PossibleMethodResults {
                val methods = FregeMethodNameIndex.INSTANCE.findByName(
                    name, project, GlobalSearchScope.everythingScope(project)
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
