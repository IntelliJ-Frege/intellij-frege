package com.plugin.frege.psi

import com.intellij.openapi.roots.ContentIterator
import com.intellij.openapi.vfs.VirtualFileFilter
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementFinder
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiPackage
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.psi.impl.FregePsiClassUtilImpl.iterateFregeFiles

class FregePsiElementFinder : PsiElementFinder() {
    override fun findClass(qualifiedName: String, scope: GlobalSearchScope): PsiClass? {
        val classes = findClasses(qualifiedName, scope)
        return classes.firstOrNull()
    }

    override fun findClasses(qualifiedName: String, scope: GlobalSearchScope): Array<PsiClass> {
        return getClasses(
            { it.qualifiedName == qualifiedName },
            scope, isInFregeLibrary(qualifiedName)
        )
    }

    override fun getClasses(psiPackage: PsiPackage, scope: GlobalSearchScope): Array<PsiClass> {
        return getClasses({ clazz ->
            val clazzName = clazz.name
            clazzName != null && psiPackage.containsClassNamed(clazzName)
        }, scope, isInFregeLibrary(psiPackage.qualifiedName))
    }

    private fun getClasses(
        predicate: (clazz: PsiClass) -> Boolean, scope: GlobalSearchScope,
        includingLibrary: Boolean
    ): Array<PsiClass> {
        val project = scope.project ?: return PsiClass.EMPTY_ARRAY
        val manager = PsiManager.getInstance(project)
        val classes: MutableList<PsiClass> = ArrayList()

        val processor = ContentIterator { virtualFile ->
            val file = manager.findFile(virtualFile)
            PsiTreeUtil.findChildrenOfType(file, FregePsiClass::class.java)
                .filter(predicate).forEach { classes.add(it) }
            true
        }
        val filter = VirtualFileFilter { true }

        iterateFregeFiles(processor, scope, filter, includingLibrary)
        return classes.toTypedArray()
    }

    private fun isInFregeLibrary(qualifiedName: String): Boolean {
        return qualifiedName.startsWith("frege")
    }
}
