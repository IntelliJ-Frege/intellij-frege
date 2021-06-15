package com.plugin.frege.linemarker.related

import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.impl.FregeNativeFunctionNameImpl
import javax.swing.Icon

class FregeNativeFunctionToDelegatedMemberLineMarker : FregeRelatedItemLineMarkerAbstract() {
    override fun getTargets(element: PsiElement): List<PsiElement> {
        val member = (element.parent as? FregeNativeFunctionNameImpl)?.getDelegatedMember()
        return listOfNotNull(member)
    }

    override val icon: Icon
        get() = AllIcons.Gutter.OverridingMethod
    override val tooltipText: String
        get() = "Navigate to Java method or field"
}
