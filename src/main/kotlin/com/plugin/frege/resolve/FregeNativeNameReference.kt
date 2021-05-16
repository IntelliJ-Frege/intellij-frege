package com.plugin.frege.resolve

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.impl.FregePsiClassUtilImpl.getClassesByQualifiedName

class FregeNativeNameReference(element: PsiElement) : FregeReferenceBase(element, TextRange(0, element.textLength)) {

    override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
        return getClassesByQualifiedName(psiElement.project, psiElement.text) // TODO support incomplete code
    }
}
