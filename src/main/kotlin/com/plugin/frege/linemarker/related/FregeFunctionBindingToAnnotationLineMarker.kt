package com.plugin.frege.linemarker.related

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.FregeIcons
import com.plugin.frege.psi.impl.FregeBindingImpl
import com.plugin.frege.psi.impl.FregeFunctionNameImpl
import javax.swing.Icon

class FregeFunctionBindingToAnnotationLineMarker : FregeRelatedItemLineMarkerAbstract() {
    override fun getTargets(element: PsiElement): List<PsiElement> {
        val parent = element.parent as? FregeFunctionNameImpl ?: return emptyList()
        val binding = PsiTreeUtil.getParentOfType(parent, FregeBindingImpl::class.java) ?: return emptyList()
        return listOfNotNull(binding.getAnnoItem())
    }

    override val icon: Icon
        get() = FregeIcons.AnnotationMarker
    override val tooltipText: String
        get() = "Navigate to type annotation"
}
