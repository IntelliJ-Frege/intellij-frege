package com.plugin.frege.linemarker.related

import com.intellij.psi.PsiElement
import com.plugin.frege.FregeIcons
import com.plugin.frege.psi.impl.FregeNativeFunctionNameImpl

class FregeNativeFunctionToDelegatedMemberLineMarker : FregeRelatedItemLineMarkerAbstract() {
    override val icon get() = FregeIcons.JavaMarker!!

    override val tooltipText get() = "Navigate to Java method or field"

    override fun getTargets(element: PsiElement): List<PsiElement> {
        val member = (element.parent as? FregeNativeFunctionNameImpl)?.getDelegatedMember()
        return listOfNotNull(member)
    }
}
