package com.plugin.frege.linemarker.related

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.util.FregePsiUtil
import javax.swing.Icon

abstract class FregeRelatedItemLineMarkerAbstract {
    fun addMarker(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (!FregePsiUtil.isLeaf(element)) {
            return
        }
        val targets = getTargets(element).ifEmpty { return }
        val markerInfo = NavigationGutterIconBuilder.create(icon)
            .setTargets(targets)
            .setTooltipText(tooltipText).createLineMarkerInfo(element)
        result.add(markerInfo)
    }

    protected abstract fun getTargets(element: PsiElement): List<PsiElement>
    protected abstract val icon: Icon
    protected abstract val tooltipText: String
}
