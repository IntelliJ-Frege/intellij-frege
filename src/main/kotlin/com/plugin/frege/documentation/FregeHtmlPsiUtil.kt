package com.plugin.frege.documentation

import com.plugin.frege.psi.FregePackageName
import com.plugin.frege.psi.FregePsiMethod
import com.plugin.frege.psi.impl.FregePsiUtilImpl

object FregeHtmlPsiUtil {

    private fun psiElementLink(fqn: String, label: String): String {
        val href = "psi_element://$fqn"
        val content = "<code>$label</code>"
        return "<a href=\"$href\">$content</a>"
    }

    fun packageNameLink(fregePackageName: FregePackageName): String {
        return psiElementLink(fregePackageName.text, fregePackageName.text)
    }

    fun methodLink(method: FregePsiMethod): String? {
        val fqn = FregePsiUtilImpl.getFullQualifiedNameOfFregePsiMethod(method) ?: return null
        return psiElementLink(fqn, method.name)
    }
}