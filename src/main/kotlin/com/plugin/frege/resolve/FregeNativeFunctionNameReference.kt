package com.plugin.frege.resolve

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfTypes
import com.plugin.frege.psi.*
import com.plugin.frege.psi.impl.FregePsiClassUtilImpl.getClassesByQualifiedName
import com.plugin.frege.psi.impl.FregePsiClassUtilImpl.getMethodsAndFieldsByName
import com.plugin.frege.psi.impl.FregePsiUtilImpl

class FregeNativeFunctionNameReference(element: PsiElement) : FregeReferenceBase(element, TextRange(0, element.textLength)) {

    // TODO take into account: signatures
    override fun resolveInner(incompleteCode: Boolean): List<PsiElement> { // TODO support incomplete code
        val nativeFunction = psiElement.parentOfTypes(FregeNativeFun::class) ?: return emptyList()

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
            val dataNameUsage = PsiTreeUtil.findChildOfType(sigma, FregeDataNameUsage::class.java)
            nativeNames = getNativeNamesFromDataNameUsage(dataNameUsage, incompleteCode)
            methodName = psiElement.text
        }

        val project = psiElement.project
        return nativeNames.asSequence()
            .flatMap { getClassesByQualifiedName(project, it) }
            .flatMap { getMethodsAndFieldsByName(it, methodName) }
            .toList()
    }

    private fun getNativeNamesFromDataNameUsage(
        dataNameUsage: FregeDataNameUsage?,
        incompleteCode: Boolean
    ): List<String> {
        return if (dataNameUsage != null) {
            FregeDataNameUsageReference(dataNameUsage).resolveInner(incompleteCode)
                .mapNotNull { getNativeNameFromData(it)?.text }
        } else {
            emptyList()
        }
    }

    private fun getNativeNameFromJavaItem(javaItem: FregeJavaItem?): PsiElement? {
        return javaItem?.nativeName
    }

    private fun getNativeNameFromData(dataNative: PsiElement): FregeNativeName? {
        if (dataNative !is FregeDataDclNative) {
            return null
        }
        return PsiTreeUtil.getChildOfType(dataNative, FregeNativeName::class.java)
    }
}
