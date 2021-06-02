package com.plugin.frege.linemarker.related

import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.impl.FregeNativeFunctionNameImpl
import javax.swing.Icon

class FregeNativeFunctionToDelegatedMemberLineMarker : FregeRelatedItemLineMarkerAbstract() {
    override fun getTargets(element: PsiElement): List<PsiElement> {
        val member = (element.parent as? FregeNativeFunctionNameImpl)?.getDelegatedMember()
        return if (member != null) listOf(member) else emptyList()
    }

    override fun getIcon(): Icon {
        return AllIcons.Gutter.OverridingMethod
    }

    override fun getTooltipText(): String {
        return "Navigate to Java method or field"
    }
}