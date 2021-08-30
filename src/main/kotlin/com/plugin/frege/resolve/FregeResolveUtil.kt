package com.plugin.frege.resolve

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentIterator
import com.intellij.openapi.vfs.VirtualFileFilter
import com.intellij.psi.*
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.parentOfTypes
import com.plugin.frege.FregeFileType
import com.plugin.frege.psi.*
import com.plugin.frege.psi.util.FregeName.Companion.fullName
import com.plugin.frege.psi.util.FregeName.Companion.isNotQualified
import com.plugin.frege.psi.util.FregeName.Companion.isQualified
import com.plugin.frege.psi.util.FregeName.Companion.merge
import com.plugin.frege.psi.util.FregeName.Companion.qualifier
import com.plugin.frege.psi.impl.FregeNamedStubBasedPsiElementBase
import com.plugin.frege.psi.util.FregePsiUtil.findElementsWithinScope
import com.plugin.frege.psi.util.FregePsiUtil.getPredicateCheckingTypeAndName
import com.plugin.frege.psi.util.FregePsiUtil.notWeakScopeOfElement
import com.plugin.frege.psi.util.FregePsiUtil.scopeOfElement
import com.plugin.frege.psi.mixin.FregeProgramUtil.imports
import com.plugin.frege.psi.util.FregeName
import com.plugin.frege.resolve.FregeImportResolveUtil.findAvailableModulesInImportsForElement
import com.plugin.frege.resolve.FregeImportResolveUtil.findClassesFromUsageInImports
import com.plugin.frege.resolve.FregeImportResolveUtil.findMethodsFromUsageInImports
import com.plugin.frege.stubs.index.FregeClassNameIndex
import com.plugin.frege.stubs.index.FregeMethodNameIndex

object FregeResolveUtil {
    /**
     * @return a list of classes in [project] with [qualifiedName].
     */
    @JvmStatic
    fun findClassesByQualifiedName(project: Project, qualifiedName: String): List<FregePsiClass> {
        return FregeClassNameIndex.findByName(
            qualifiedName, project, GlobalSearchScope.everythingScope(project)
        )
    }

    /**
     * @return a list of methods and fields with [name] in [psiClass].
     */
    @JvmStatic
    fun findMethodsAndFieldsByName(psiClass: PsiClass, name: String): List<PsiMember> {
        val methods = psiClass.findMethodsByName(name, true)
        if (methods.isNotEmpty()) {
            return methods.toList()
        }
        val field = psiClass.findFieldByName(name, true)
        return if (field != null) listOf(field) else emptyList()
    }

    /**
     * @return the nearest containing class of [element].
     */
    @JvmStatic
    fun findContainingFregeClass(element: PsiElement): FregePsiClass? {
        return (element as? FregeNamedStubBasedPsiElementBase<*>)?.greenStub?.parentStub?.psi as? FregePsiClass
            ?: element.parentOfTypes(FregePsiClass::class, withSelf = false)
    }

    /**
     * @return all methods in [project] with [qualifiedName].
     * @param qualifiedName merged name of class and usage.
     * @see [findMethodsInClassesInCurrentFile]
     */
    private fun findMethodsByQualifiedName(
        project: Project,
        qualifiedName: FregeName
    ): List<FregePsiMethod> {
        if (qualifiedName.isNotQualified) {
            return emptyList()
        }
        return FregeMethodNameIndex.findByName(qualifiedName.shortName, project, GlobalSearchScope.everythingScope(project))
            .filter { method ->
                if (qualifiedName.isNotQualified && method.onlyQualifiedSearch()) {
                    return@filter false
                }
                val className = method.containingClass?.let { FregeName.ofPsiMember(it) } ?: return@filter false
                className.fullName == qualifiedName.qualifier
            }.ifEmpty {
                qualifiedName.qualifier?.let { qualifier ->
                    findClassesByQualifiedName(project, qualifier).flatMap { clazz ->
                        clazz.findMethodsByName(qualifiedName.shortName, true)
                            .asSequence()
                            .filterIsInstance<FregePsiMethod>()
                            .filter { qualifiedName.isQualified || !it.onlyQualifiedSearch() }
                    }
                } ?: emptyList()
            }
    }

    /**
     * Iterates over all the Frege files in [scope] and filters with [filter].
     * After that applies [processor].
     */
    @JvmStatic
    fun iterateFregeFiles(
        processor: ContentIterator,
        scope: GlobalSearchScope,
        filter: VirtualFileFilter
    ) {
        val project = scope.project ?: return
        DumbService.getInstance(project).runReadActionInSmartMode {
            val files = FileTypeIndex.getFiles(FregeFileType.INSTANCE, scope)
            for (virtualFile in files) {
                if (filter.accept(virtualFile!!) && !processor.processFile(virtualFile)) {
                    break
                }
            }
        }
    }

    /**
     * Util method for getting the first binding from psi element presenting a name of binding.
     */
    @JvmStatic
    fun resolveBindingByNameElement(bindingName: PsiElement, incompleteCode: Boolean): List<PsiElement> {
        val scope = notWeakScopeOfElement(bindingName) ?: return emptyList()
        val binding = findElementsWithinScope(
            scope,
            getPredicateCheckingTypeAndName(FregeBinding::class, FregeName(bindingName), incompleteCode)
        ).minByOrNull { it.textOffset }
        return if (binding != null) listOf(binding) else emptyList()
    }

    /**
     * @return list of [FregePsiClass] which are in the global scope of [element].
     */
    @JvmStatic
    fun findClassesInCurrentFile(element: PsiElement): List<FregePsiClass> {
        val module = element.parentOfType<FregeProgram>(true) ?: return emptyList()
        val body = module.body ?: return emptyList()
        return body.topDeclList.mapNotNull { it.firstChild as? FregePsiClass } + module
    }

    /**
     * First of all tries to get qualified name. After that searches for methods in:
     * * Methods declared in current scope of [usage] and outer ones
     * * Methods in classes declared in the file of [usage]
     * * Methods in classes imported in the file of [usage]
     */
    @JvmStatic
    fun findMethodsFromUsage(
        usage: PsiElement,
        incompleteCode: Boolean
    ): List<PsiElement> {
        val result = findBindings(usage, incompleteCode).toMutableList()
        if (!incompleteCode && result.isEmpty()) {
            result += findMethodsInClassesInCurrentFile(usage).ifEmpty {
                findMethodsFromUsageInImports(usage)
            }
        } else if (incompleteCode) {
            result += findClassesInCurrentFile(usage).flatMap {
                it.methods.asSequence()
            }
            result += findAllAvailableClassesInImportsForElement(usage.project, usage).flatMap {
                it.methods.asSequence()
            }
        }
        return result.distinct()
    }

    private fun findMethodsInClassesInCurrentFile(usage: PsiElement): List<PsiMethod> {
        val usageName = FregeName(usage)
        val project = usage.project
        val availableClasses = findClassesInCurrentFile(usage)
        for (clazz in availableClasses) {
            val className = FregeName.ofPsiMember(clazz) ?: continue
            val qualifiedName = className.merge(usageName) ?: continue
            val methods = findMethodsByQualifiedName(project, qualifiedName).toMutableList()
            methods.removeIf { usageName.isNotQualified && it.onlyQualifiedSearch() }
            if (methods.isNotEmpty()) {
                return methods // TODO errors if several references in different classes
            }
        }
        return emptyList()
    }

    private fun findBindings(
        usage: PsiElement,
        incompleteCode: Boolean
    ): List<PsiElement> {
        val predicate = getPredicateCheckingTypeAndName(FregeBinding::class, FregeName(usage), incompleteCode)
        var scope: PsiElement? = scopeOfElement(usage)
        while (scope != null) {
            val functionNames = findElementsWithinScope(scope, predicate)
            if (functionNames.isNotEmpty()) {
                return listOf(functionNames.minByOrNull { it.textOffset }!!)
            }
            scope = scopeOfElement(scope.parent)
        }
        return emptyList()
    }

    /**
     * Searches for classes in the current file and imported classes.
     */
    @JvmStatic
    fun findClassesFromUsage(
        usage: PsiElement,
        incompleteCode: Boolean
    ): List<PsiElement> {
        val results = tryFindClassesInCurrentFileFromUsage(usage, incompleteCode).toMutableList()
        results += if (!incompleteCode) {
            findClassesFromUsageInImports(usage)
        } else {
            findAllAvailableClassesInImportsForElement(usage.project, usage)
        }

        val module = usage.parentOfType<FregeProgram>()
        if (module != null) {
            val name = FregeName(usage)
            val imports = module.imports
            results += tryFindClassesInImportsAliases(name, imports, incompleteCode)
            if (module.name == name.shortName) {
                results += module
            }
        }
        return results.distinct()
    }

    private fun tryFindClassesInCurrentFileFromUsage(
        usage: PsiElement,
        incompleteCode: Boolean
    ): List<PsiElement> {
        val name = FregeName(usage)
        val classes = findClassesInCurrentFile(usage).toMutableList()
        if (!incompleteCode) {
            val moduleName = usage.parentOfType<FregeProgram>()?.name
            classes.removeIf {
                name.isQualified && name.qualifier != moduleName || name.shortName != it.name
            }
        }
        return classes
    }

    private fun tryFindClassesInImportsAliases(
        name: FregeName,
        imports: List<FregeImportDecl>,
        incompleteCode: Boolean
    ): List<PsiElement> {
        return imports
            .asSequence()
            .mapNotNull { it.importDeclAlias }
            .filter { incompleteCode || it.name == name.shortName }
            .toList()
    }

    private fun findAllAvailableClassesInImportsForElement(
        project: Project,
        element: PsiElement
    ): List<FregePsiClass> {
        val modules = findAvailableModulesInImportsForElement(project, element)
        val inners = modules.flatMap { findClassesInCurrentFile(it) }
        return modules + inners
    }
}
