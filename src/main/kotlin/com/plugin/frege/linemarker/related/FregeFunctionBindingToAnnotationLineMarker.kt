package com.plugin.frege.linemarker.related

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.FregeIcons
import com.plugin.frege.psi.FregeFunctionLhs
import com.plugin.frege.psi.impl.FregeBindingImpl
import javax.swing.Icon

class FregeFunctionBindingToAnnotationLineMarker : FregeRelatedItemLineMarkerAbstract() {
    override val icon get(): Icon = FregeIcons.AnnotationMarker

    override val tooltipText get(): String = "Navigate to type annotation"

    override fun getTargets(element: PsiElement): List<PsiElement> {
        val parent = tryGetFunctionLhs(element) ?: return emptyList()
        val binding = PsiTreeUtil.getParentOfType(parent, FregeBindingImpl::class.java) ?: return emptyList()
        return if (binding.nameIdentifier === element) listOfNotNull(binding.getAnnoItem()) else emptyList()
    }

    private fun tryGetFunctionLhs(element: PsiElement): FregeFunctionLhs? {
        return generateSequence(element.parent) { it.parent }.take(3)
            .filterIsInstance<FregeFunctionLhs>().firstOrNull()
    }
}
