package com.plugin.frege.resolve

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.java.stubs.index.JavaFullClassNameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.plugin.frege.psi.FregeNativeName
import com.plugin.frege.psi.util.FregePsiUtil.nameFromQualifiedName
import com.plugin.frege.psi.util.FregePsiUtil.qualifierFromQualifiedName
import com.plugin.frege.resolve.FregeResolveUtil.findMethodsAndFieldsByName

@Suppress("UnstableApiUsage")
class FregeNativeNameReference(element: PsiElement) : FregeReferenceBase(element, TextRange(0, element.textLength)) {

    override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
        val qualifiedName = getJavaName()
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

    private fun getJavaName(): String {
        val nativeName = psiElement as? FregeNativeName
            ?: throw IllegalStateException("Element for native name reference must implement native name.")
        val stringLiteral = nativeName.stringLiteral
        val text = nativeName.text
        if (stringLiteral == null) {
            return text
        }
        val stringText = stringLiteral.text
        val stringTextWithoutQuotes = stringText.substring(1, stringText.length - 1)
        val beforeQuote = text.substringBefore('"').substringBeforeLast('.')
        return if (beforeQuote.isEmpty()) {
            stringTextWithoutQuotes
        } else {
            "$beforeQuote.$stringTextWithoutQuotes"
        }
    }
}
