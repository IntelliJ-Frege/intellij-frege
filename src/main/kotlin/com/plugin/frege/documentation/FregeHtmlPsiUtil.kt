package com.plugin.frege.documentation

import com.intellij.codeInsight.documentation.DocumentationManagerUtil
import com.plugin.frege.psi.FregePackageName
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.FregePsiMethod

object FregeHtmlPsiUtil {

    private fun psiElementLink(fqn: String, label: String): String {
        return buildString {
            DocumentationManagerUtil.createHyperlink(this, fqn, label, false)
        }
    }

    fun packageNameLink(fregePackageName: FregePackageName): String {
        return psiElementLink(fregePackageName.text, fregePackageName.text)
    }

    fun psiMethodLink(method: FregePsiMethod): String? {
        val classQualifiedName = method.containingClass?.qualifiedName ?: return null
        return psiElementLink(classQualifiedName + "#" + method.name, method.name)
    }

    fun psiClassLink(psiClass: FregePsiClass): String? {
        val classQualifiedName = psiClass.qualifiedName ?: return null
        val className = psiClass.name ?: return null
        return psiElementLink(classQualifiedName, className)
    }
}
