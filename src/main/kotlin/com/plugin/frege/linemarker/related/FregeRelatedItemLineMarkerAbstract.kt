package com.plugin.frege.linemarker.related

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import javax.swing.Icon

abstract class FregeRelatedItemLineMarkerAbstract {
    fun addMarker(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val targets = getTargets(element)
        if (targets.isEmpty()) {
            return
        }
        val markerInfo = NavigationGutterIconBuilder.create(icon)
            .setTargets(getTargets(element))
            .setTooltipText(tooltipText).createLineMarkerInfo(element)
        result.add(markerInfo)
    }

    protected abstract fun getTargets(element: PsiElement): List<PsiElement>
    protected abstract val icon: Icon
    protected abstract val tooltipText: String
}
