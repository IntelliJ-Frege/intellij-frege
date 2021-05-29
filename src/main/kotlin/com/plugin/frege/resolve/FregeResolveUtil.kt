package com.plugin.frege.resolve

import com.intellij.openapi.module.impl.scopes.LibraryScope
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentIterator
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFileFilter
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.parentOfTypes
import com.plugin.frege.FregeFileType
import com.plugin.frege.psi.FregeBinding
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.impl.FregePsiUtilImpl
import com.plugin.frege.psi.impl.FregePsiUtilImpl.findClassesInCurrentFile
import com.plugin.frege.psi.impl.FregePsiUtilImpl.findElementsWithinScope
import com.plugin.frege.psi.impl.FregePsiUtilImpl.findImportsNamesForElement
import com.plugin.frege.psi.impl.FregePsiUtilImpl.findWhereInExpression
import com.plugin.frege.psi.impl.FregePsiUtilImpl.getByTypePredicateCheckingName
import com.plugin.frege.psi.impl.FregePsiUtilImpl.getQualifiedNameFromUsage
import com.plugin.frege.psi.impl.FregePsiUtilImpl.mergeQualifiedNames
import com.plugin.frege.psi.impl.FregePsiUtilImpl.scopeOfElement
import com.plugin.frege.stubs.index.FregeClassNameIndex
import com.plugin.frege.stubs.index.FregeMethodNameIndex

object FregeResolveUtil {
    /**
     * @return a list of classes in [project] with [qualifiedName].
     */
    @JvmStatic
    fun findClassesByQualifiedName(project: Project, qualifiedName: String): List<PsiClass> {
        val classes = FregeClassNameIndex.INSTANCE.findByName(
            qualifiedName, project, GlobalSearchScope.everythingScope(project)
        )

        if (classes.isNotEmpty()) {
            return classes
        }
        val inProject = doFindClasses(qualifiedName, GlobalSearchScope.projectScope(project))
        return inProject.ifEmpty {
            doFindClasses(qualifiedName, LibraryScope.everythingScope(project))
        }
    }

    private fun doFindClasses(qualifiedName: String, scope: GlobalSearchScope): List<PsiClass> {
        val project = scope.project ?: return emptyList()
        return JavaPsiFacade.getInstance(project).findClasses(qualifiedName, scope).toList()
    }

    /**
     * @return a list of methods and fields with [name] in [psiClass].
     */
    @JvmStatic
    fun findMethodsAndFieldsByName(psiClass: PsiClass, name: String): List<PsiElement> {
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
        return element.parentOfTypes(FregePsiClass::class, withSelf = false)
    }

    /**
     * @return all methods in [project] with [qualifiedName].
     */
    @JvmStatic
    fun findMethodsByQualifiedName(project: Project, qualifiedName: String): List<PsiMethod> {
        val name = FregePsiUtilImpl.nameFromQualifiedName(qualifiedName)
        val qualifier = FregePsiUtilImpl.qualifierFromQualifiedName(qualifiedName)
        if (qualifier.isEmpty()) {
            return emptyList()
        }

        return FregeMethodNameIndex.INSTANCE.findByName(name, project, GlobalSearchScope.everythingScope(project))
            .filter { method ->
                val containingClass = method.containingClass // TODO store this in stub
                containingClass?.qualifiedName == qualifier
            }.ifEmpty {
                findClassesByQualifiedName(project, qualifier)
                    .flatMap { it.findMethodsByName(name, true).asSequence() }
            }
    }

    /**
     * @return all methods in [project] in the import with [importName].
     */
    @JvmStatic
    fun findAllMethodsByImportName(project: Project, importName: String): List<PsiMethod> {
        return if (importName.isNotEmpty()) {
            findClassesByQualifiedName(project, importName).flatMap { it.allMethods.asSequence() }
        } else {
            emptyList()
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
        filter: VirtualFileFilter,
        includingLibraries: Boolean
    ) {
        val project = scope.project ?: return
        DumbService.getInstance(project).runReadActionInSmartMode {
            if (includingLibraries) {
                val files = FileTypeIndex.getFiles(FregeFileType.INSTANCE, scope)
                for (virtualFile in files) {
                    if (filter.accept(virtualFile!!) && !processor.processFile(virtualFile)) {
                        break
                    }
                }
            } else {
                ProjectFileIndex.getInstance(project).iterateContent(processor, filter)
            }
        }
    }

    /**
     * Util method for getting the first binding from psi element presenting a name of binding.
     */
    @JvmStatic
    fun resolveBindingByNameElement(bindingName: PsiElement, incompleteCode: Boolean): List<PsiElement> {
        val binding = findElementsWithinScope(
            bindingName,
            getByTypePredicateCheckingName(FregeBinding::class, bindingName.text, incompleteCode)
        ).minByOrNull { it.textOffset }
        return if (binding != null) listOf(binding) else emptyList()
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
        if (result.isNotEmpty() && !incompleteCode) {
            return result
        }

        result.addAll(findMethodsInClassesInCurrentFile(qualifiedName, usage)) // TODO incomplete code
        if (result.isNotEmpty() && !incompleteCode) {
            return result
        }

        result.addAll(findMethodsByImports(qualifiedName, usage, incompleteCode))
        return result
    }

    private fun findMethodsByImports(
        name: String,
        usage: PsiElement,
        incompleteCode: Boolean
    ): List<PsiMethod> {
        val project = usage.project
        val imports = findImportsNamesForElement(usage, true)
        val methods = mutableListOf<PsiMethod>()
        for (currentImport in imports) {
            if (incompleteCode) {
                methods.addAll(findAllMethodsByImportName(project, currentImport))
            } else {
                val qualifiedName = mergeQualifiedNames(currentImport, name)
                methods.addAll(findMethodsByQualifiedName(project, qualifiedName))
                if (methods.isNotEmpty()) {
                    break
                }
            }
        }
        return methods
    }

    private fun findMethodsInClassesInCurrentFile(
        name: String,
        usage: PsiElement,
    ): List<PsiMethod> {
        val project = usage.project
        val availableClasses = findClassesInCurrentFile(usage)
        for (clazz in availableClasses) {
            val className = clazz.qualifiedName ?: continue
            val qualifiedName = mergeQualifiedNames(className, name)
            val methods = findMethodsByQualifiedName(project, qualifiedName)
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

        // check if this expression has `where` ans search there for definitions if it does.
        val where = findWhereInExpression(usage)
        if (where?.linearIndentSection != null) {
            val whereFuncNames = findElementsWithinScope(where.linearIndentSection!!, predicate)
            if (whereFuncNames.isNotEmpty()) {
                return whereFuncNames
            }
        }

        // search for definitions in the current and outer scopes
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
}
