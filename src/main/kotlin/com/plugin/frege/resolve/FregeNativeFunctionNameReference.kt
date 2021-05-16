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
        val nativeFunction = psiElement.parentOfTypes(FregeNativeFun::class) ?: return listOf()

        val nativeNames: List<String>
        val methodName: String
        val nativeNameFromJavaItem = getNativeNameFromJavaItem(nativeFunction.javaItem)
        if (nativeNameFromJavaItem != null) {
            val nativeNameString = nativeNameFromJavaItem.text

            methodName = FregePsiUtilImpl.nameFromQualifiedName(nativeNameString)
            nativeNames = if (nativeNameString.contains(".")) {
                listOf(FregePsiUtilImpl.qualifierFromQualifiedName(nativeNameString))
            } else {
                listOf()
            }
        } else {
            val sigmas = nativeFunction.sigmaList
            if (sigmas.isEmpty()) {
                return listOf()
            }

            val sigma = sigmas[0]
            val dataNameUsage = PsiTreeUtil.findChildOfType(sigma, FregeDataNameUsage::class.java)
            nativeNames = getNativeNamesFromDataNameUsage(dataNameUsage, incompleteCode)
            methodName = psiElement.text
        }

        val project = psiElement.project
        return nativeNames.asSequence()
            .flatMap { name -> getClassesByQualifiedName(project, name) }
            .flatMap { psiClass -> getMethodsAndFieldsByName(psiClass, methodName) }
            .toList()
    }

    private fun getNativeNamesFromDataNameUsage(
        dataNameUsage: FregeDataNameUsage?,
        incompleteCode: Boolean
    ): List<String> {
        return if (dataNameUsage != null) {
            FregeDataNameUsageReference(dataNameUsage).resolveInner(incompleteCode)
                .mapNotNull { dataName -> getNativeNameFromData(dataName)?.text }
        } else {
            listOf()
        }
    }

    private fun getNativeNameFromJavaItem(javaItem: FregeJavaItem?): PsiElement? {
        return javaItem?.nativeName
    }

    private fun getNativeNameFromData(dataName: PsiElement): FregeNativeName? {
        if (dataName !is FregeDataNameNative) {
            return null
        }
        val dataNative = PsiTreeUtil.getParentOfType(dataName, FregeDataDclNative::class.java)
            ?: return null
        return PsiTreeUtil.getChildOfType(dataNative, FregeNativeName::class.java)
    }
}
