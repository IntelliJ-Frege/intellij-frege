package com.plugin.frege.resolve

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfTypes
import com.plugin.frege.psi.*
import com.plugin.frege.psi.impl.FregePsiUtilImpl
import com.plugin.frege.resolve.FregeResolveUtil.findClassesByQualifiedName
import com.plugin.frege.resolve.FregeResolveUtil.findMethodsAndFieldsByName

class FregeNativeFunctionNameReference(element: PsiElement) : FregeReferenceBase(element, TextRange(0, element.textLength)) {

    // TODO take into account: signatures
    override fun resolveInner(incompleteCode: Boolean): List<PsiElement> { // TODO support incomplete code
        val nativeFunction = psiElement.parentOfTypes(FregeNativeFunction::class) ?: return emptyList()

        val nativeNames: List<String>
        val methodName: String
        val nativeNameFromJavaItem = getNativeNameFromJavaItem(nativeFunction.javaItem)
        if (nativeNameFromJavaItem != null) {
            val nativeNameString = nativeNameFromJavaItem.text

            methodName = FregePsiUtilImpl.nameFromQualifiedName(nativeNameString)
            nativeNames = if (nativeNameString.contains(".")) {
                listOf(FregePsiUtilImpl.qualifierFromQualifiedName(nativeNameString))
            } else {
                emptyList()
            }
        } else {
            val sigmas = nativeFunction.sigmaList
            if (sigmas.isEmpty()) {
                return emptyList()
            }

            val sigma = sigmas[0]
            val dataNameUsage = PsiTreeUtil.findChildOfType(sigma, FregeConidUsage::class.java)
            nativeNames = getNativeNamesFromDataNameUsage(dataNameUsage, incompleteCode)
            methodName = psiElement.text
        }

        val project = psiElement.project
        return nativeNames.asSequence()
            .flatMap { findClassesByQualifiedName(project, it) }
            .flatMap { findMethodsAndFieldsByName(it, methodName) }
            .toList()
    }

    private fun getNativeNamesFromDataNameUsage(
        dataNameUsage: FregeConidUsage?,
        incompleteCode: Boolean
    ): List<String> {
        return if (dataNameUsage != null) {
            FregeConidUsageReference(dataNameUsage).resolveInner(incompleteCode)
                .mapNotNull { getNativeNameFromData(it)?.text }
        } else {
            emptyList()
        }
    }

    private fun getNativeNameFromJavaItem(javaItem: FregeJavaItem?): PsiElement? {
        return javaItem?.nativeName
    }

    private fun getNativeNameFromData(dataNative: PsiElement): FregeNativeName? {
        return if (dataNative is FregeNativeDataDecl) {
            PsiTreeUtil.getChildOfType(dataNative, FregeNativeName::class.java)
        } else {
            null
        }
    }
}
