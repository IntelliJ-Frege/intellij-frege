package com.plugin.frege.resolve

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.java.stubs.index.JavaFullClassNameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.plugin.frege.psi.impl.FregePsiUtilImpl.nameFromQualifiedName
import com.plugin.frege.psi.impl.FregePsiUtilImpl.qualifierFromQualifiedName
import com.plugin.frege.resolve.FregeResolveUtil.findMethodsAndFieldsByName

@Suppress("UnstableApiUsage")
class FregeNativeNameReference(element: PsiElement) : FregeReferenceBase(element, TextRange(0, element.textLength)) {

    override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
        val qualifiedName = psiElement.text
        val project = psiElement.project
        val classes = findJavaClasses(qualifiedName, project)
        if (classes.isNotEmpty()) {
            return classes
        }

        val name = nameFromQualifiedName(qualifiedName)
        val qualifier = qualifierFromQualifiedName(qualifiedName)
        return findJavaClasses(qualifier, project).flatMap { findMethodsAndFieldsByName(it, name) }
    }

    private fun findJavaClasses(qualifiedName: String, project: Project): List<PsiClass> {
        return JavaFullClassNameIndex.getInstance().get(
            qualifiedName.hashCode(),
            project,
            GlobalSearchScope.everythingScope(project)
        ).filter { it.qualifiedName == qualifiedName } // TODO support incomplete code
    }
}
