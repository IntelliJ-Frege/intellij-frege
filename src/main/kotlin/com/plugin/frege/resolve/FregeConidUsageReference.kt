package com.plugin.frege.resolve

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.plugin.frege.psi.FregeElementFactory.createConidUsage
import com.plugin.frege.psi.impl.FregeNamedStubBasedPsiElementBase
import com.plugin.frege.resolve.FregeResolveUtil.findClassesFromUsage
import com.plugin.frege.resolve.FregeResolveUtil.findMethodsFromUsage

class FregeConidUsageReference(element: PsiElement) : FregeReferenceBase(element, TextRange(0, element.textLength)) {
    override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
        val results = findClassesFromUsage(psiElement, incompleteCode).toMutableList()
        if (incompleteCode || results.isEmpty()) {
            results.addAll(findMethodsFromUsage(psiElement, incompleteCode))
        }

        return results.filter { it !is FregeNamedStubBasedPsiElementBase<*> || it.canBeReferenced }
    }

    override fun bindToElement(element: PsiElement): PsiElement {
        return if (element is PsiNamedElement) {
            val newName = element.name
            val oldName = psiElement.text
            if (newName != null && oldName != newName) {
                return handleElementRename(newName)
            } else {
                psiElement
            }
        } else {
            super.bindToElement(element)
        }
    }

    override fun handleElementRename(name: String): PsiElement {
        return psiElement.replace(createConidUsage(psiElement.project, name))
    }
}
