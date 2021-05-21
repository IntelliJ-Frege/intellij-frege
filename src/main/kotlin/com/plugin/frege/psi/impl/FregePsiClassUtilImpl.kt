package com.plugin.frege.psi.impl

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
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.FregePsiClassHolder
import com.plugin.frege.stubs.index.FregeClassNameIndex
import com.plugin.frege.stubs.index.FregeMethodNameIndex

object FregePsiClassUtilImpl {
    /**
     * @return a list of classes in [project] with [qualifiedName].
     */
    @JvmStatic
    fun getClassesByQualifiedName(project: Project, qualifiedName: String): List<PsiClass> {
        val classes = FregeClassNameIndex.getInstance().get(
            qualifiedName, project, GlobalSearchScope.everythingScope(project)
        ).toList()

        if (classes.isNotEmpty()) {
            return classes
        }
        val inProject = doFindClasses(qualifiedName, GlobalSearchScope.projectScope(project))
        return inProject.ifEmpty {
            doFindClasses(qualifiedName, LibraryScope.everythingScope(project))
        }
    }

    private fun doFindClasses(qualifiedName: String, scope: GlobalSearchScope): List<PsiClass> {
        if (scope.project == null) {
            return emptyList()
        }
        return JavaPsiFacade.getInstance(scope.project!!).findClasses(qualifiedName, scope).toList()
    }

    /**
     * @return a list of methods and fields with [name] in [psiClass].
     */
    @JvmStatic
    fun getMethodsAndFieldsByName(psiClass: PsiClass, name: String): List<PsiElement> {
        val methods = psiClass.findMethodsByName(name, true)
        if (methods.isNotEmpty()) {
            return methods.toList()
        }
        val field = psiClass.findFieldByName(name, true)
        return if (field != null) listOf(field) else emptyList()
    }

    /**
     * @return the nearest containing class of [element].
     * @see [FregePsiClassHolder].
     */
    @JvmStatic
    fun findContainingFregeClass(element: PsiElement): FregePsiClass? {
        val holder = element.parentOfTypes(FregePsiClassHolder::class, withSelf = true) ?: return null
        if (element is FregePsiClass) { // in order not to return the same class
            val parent = holder.parent
            return if (parent != null) findContainingFregeClass(parent) else null
        }
        return holder.holdingClass
    }

    /**
     * @return all methods in [project] with [qualifiedName].
     */
    @JvmStatic
    fun getMethodsByQualifiedName(project: Project, qualifiedName: String): List<PsiMethod> {
        val name = FregePsiUtilImpl.nameFromQualifiedName(qualifiedName)
        val qualifier = FregePsiUtilImpl.qualifierFromQualifiedName(qualifiedName)
        if (qualifier.isEmpty()) {
            return emptyList()
        }

        val methodsByName =
            FregeMethodNameIndex.getInstance().get(name, project, GlobalSearchScope.everythingScope(project))
                .filter { method ->
                    val containingClass = method.containingClass // TODO store this in stub
                    containingClass?.qualifiedName == qualifier
                }

        return methodsByName.ifEmpty {
            getClassesByQualifiedName(project, qualifier)
                .flatMap { it.findMethodsByName(name, true).toList() }
        }
    }

    /**
     * @return all methods in [project] in the import with [importName].
     */
    @JvmStatic
    fun getAllMethodsByImportName(project: Project, importName: String): List<PsiMethod> {
        return if (importName.isNotEmpty()) {
            getClassesByQualifiedName(project, importName).flatMap { it.allMethods.toList() }
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
        processor: ContentIterator, scope: GlobalSearchScope,
        filter: VirtualFileFilter, includingLibraries: Boolean
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
}
