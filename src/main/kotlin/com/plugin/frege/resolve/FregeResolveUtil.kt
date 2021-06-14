package com.plugin.frege.resolve

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentIterator
import com.intellij.openapi.vfs.VirtualFileFilter
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMember
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.parentOfTypes
import com.plugin.frege.FregeFileType
import com.plugin.frege.psi.*
import com.plugin.frege.psi.impl.FregeNamedStubBasedPsiElementBase
import com.plugin.frege.psi.impl.FregePsiUtilImpl.findElementsWithinScope
import com.plugin.frege.psi.impl.FregePsiUtilImpl.getByTypePredicateCheckingName
import com.plugin.frege.psi.impl.FregePsiUtilImpl.getQualifiedNameFromUsage
import com.plugin.frege.psi.impl.FregePsiUtilImpl.isNameQualified
import com.plugin.frege.psi.impl.FregePsiUtilImpl.mergeQualifiedNames
import com.plugin.frege.psi.impl.FregePsiUtilImpl.nameFromQualifiedName
import com.plugin.frege.psi.impl.FregePsiUtilImpl.notWeakScopeOfElement
import com.plugin.frege.psi.impl.FregePsiUtilImpl.qualifierFromQualifiedName
import com.plugin.frege.psi.impl.FregePsiUtilImpl.scopeOfElement
import com.plugin.frege.psi.mixin.FregeProgramUtil.imports
import com.plugin.frege.resolve.FregeImportResolveUtil.findAvailableModulesInImportsForElement
import com.plugin.frege.resolve.FregeImportResolveUtil.findClassesFromUsageInImports
import com.plugin.frege.stubs.index.FregeClassNameIndex
import com.plugin.frege.stubs.index.FregeMethodNameIndex

object FregeResolveUtil {
    /**
     * @return a list of classes in [project] with [qualifiedName].
     */
    @JvmStatic
    fun findClassesByQualifiedName(project: Project, qualifiedName: String): List<FregePsiClass> {
        return FregeClassNameIndex.INSTANCE.findByName(
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
     */
    @JvmStatic
    fun findMethodsByQualifiedName(
        project: Project,
        qualifiedName: String,
        usageIsQualified: Boolean
    ): List<FregePsiMethod> {
        val name = nameFromQualifiedName(qualifiedName)
        val qualifier = qualifierFromQualifiedName(qualifiedName)
        if (qualifier.isEmpty()) {
            return emptyList()
        }

        return FregeMethodNameIndex.INSTANCE.findByName(name, project, GlobalSearchScope.everythingScope(project))
            .filter { method ->
                if (!usageIsQualified && method.onlyQualifiedSearch()) {
                    return@filter false
                }
                val clazz = method.containingClass ?: return@filter false
                val className = clazz.qualifiedName ?: return@filter false
                className == qualifier || (!usageIsQualified && qualifierFromQualifiedName(className) == qualifier)
            }.ifEmpty {
                findClassesByQualifiedName(project, qualifier).flatMap { clazz ->
                    clazz.findMethodsByName(name, true).asSequence()
                        .filterIsInstance<FregePsiMethod>()
                        .filter { usageIsQualified || !it.onlyQualifiedSearch() }
                }
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
            getByTypePredicateCheckingName(FregeBinding::class, bindingName.text, incompleteCode)
        ).minByOrNull { it.textOffset }
        return if (binding != null) listOf(binding) else emptyList()
    }

    /**
     * @return list of [FregePsiClass] which are in the global scope of [element].
     */
    @JvmStatic
    fun findClassesInCurrentFile(element: PsiElement): List<FregePsiClass> {
        val globalScope = element.parentOfType<FregeProgram>(true)?.body ?: return emptyList()
        return globalScope.topDeclList.mapNotNull { it.firstChild as? FregePsiClass }
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
        val qualifiedName = getQualifiedNameFromUsage(usage)
        val result = findBindings(qualifiedName, usage, incompleteCode).toMutableList()
        if (!incompleteCode) {
            result += findMethodsInClassesInCurrentFile(qualifiedName, usage)
            result += findMethodsByImports(qualifiedName, usage)
        } else {
            result += findClassesInCurrentFile(usage).flatMap {
                it.methods.asSequence()
            }
            result += findAllAvailableClassesInImportsForElement(usage.project, usage).flatMap {
                it.methods.asSequence()
            }
        }
        return result
    }

    private fun findMethodsByImports(
        name: String,
        usage: PsiElement,
    ): List<PsiMethod> {
        val project = usage.project
        val usageQualified = isNameQualified(name)
        val modules = findAvailableModulesInImportsForElement(project, usage)
        val methods = mutableListOf<PsiMethod>()
        for (module in modules) {
            val moduleName = module.qualifiedName ?: continue
            val qualifiedName = mergeQualifiedNames(moduleName, name)
            methods.addAll(findMethodsByQualifiedName(project, qualifiedName, usageQualified))
            if (methods.isNotEmpty()) {
                break
            }
        }
        return methods
    }

    private fun findMethodsInClassesInCurrentFile(
        name: String,
        usage: PsiElement,
    ): List<PsiMethod> {
        val project = usage.project
        val usageQualified = isNameQualified(name)
        val availableClasses = findClassesInCurrentFile(usage)
        for (clazz in availableClasses) {
            val className = clazz.qualifiedName ?: continue
            val qualifiedName = mergeQualifiedNames(className, name)
            val methods = findMethodsByQualifiedName(project, qualifiedName, usageQualified).toMutableList()
            methods.removeIf { !usageQualified && it.onlyQualifiedSearch() }
            if (methods.isNotEmpty()) {
                return methods // TODO errors if several references in different classes
            }
        }

        return emptyList()
    }

    private fun findBindings(
        name: String,
        usage: PsiElement,
        incompleteCode: Boolean
    ): List<PsiElement> {
        val predicate = getByTypePredicateCheckingName(FregeBinding::class, name, incompleteCode)

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
            val name = usage.text
            val imports = module.imports
            results += tryFindClassesInImportsAliases(name, imports, incompleteCode)
            if (module.name == name) {
                results += module
            }
        }
        return results
    }

    private fun tryFindClassesInCurrentFileFromUsage(
        usage: PsiElement,
        incompleteCode: Boolean
    ): List<PsiElement> {
        val name = usage.text
        val classes = findClassesInCurrentFile(usage).toMutableList()
        if (!incompleteCode) {
            val qualifiedName = getQualifiedNameFromUsage(usage)
            val qualifier = qualifierFromQualifiedName(qualifiedName)
            val isQualified = isNameQualified(qualifiedName)
            val moduleName = usage.parentOfType<FregeProgram>()?.name
            classes.removeIf {
                isQualified && qualifier != moduleName || name != it.name
            }
        }
        return classes
    }

    private fun tryFindClassesInImportsAliases(
        name: String,
        imports: List<FregeImportDecl>,
        incompleteCode: Boolean
    ): List<PsiElement> {
        return imports
            .asSequence()
            .mapNotNull { it.importDeclAlias }
            .filter { incompleteCode || it.name == name }
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
