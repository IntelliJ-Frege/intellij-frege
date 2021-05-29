package com.plugin.frege.resolve

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.java.stubs.index.JavaFullClassNameIndex
import com.intellij.psi.search.GlobalSearchScope

class FregeNativeNameReference(element: PsiElement) : FregeReferenceBase(element, TextRange(0, element.textLength)) {

    override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
        val name = psiElement.text
        val project = psiElement.project
        return JavaFullClassNameIndex.getInstance().get(
            name.hashCode(),
            project,
            GlobalSearchScope.everythingScope(project)
        ).filter { it.qualifiedName == name } // TODO support incomplete code
    }
}
