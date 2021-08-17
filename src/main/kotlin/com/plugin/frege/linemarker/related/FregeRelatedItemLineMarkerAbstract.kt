package com.plugin.frege.linemarker.related

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMember
import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.FregeProgram
import com.plugin.frege.psi.util.FregePsiUtil
import javax.swing.Icon

abstract class FregeRelatedItemLineMarkerAbstract {
    protected abstract val icon: Icon

    protected abstract val tooltipText: String

    protected abstract fun getTargets(element: PsiElement): List<PsiElement>

    protected open val cellRenderer get(): FregePsiElementCellRenderer = FregePsiElementCellRenderer.INSTANCE

    fun addMarker(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (!FregePsiUtil.isLeaf(element)) {
            return
        }
        val targets = getTargets(element).ifEmpty { return }
        val markerInfo = NavigationGutterIconBuilder
            .create(icon)
            .setCellRenderer(cellRenderer)
            .setTargets(targets)
            .setTooltipText(tooltipText)
            .createLineMarkerInfo(element)
        result.add(markerInfo)
    }

    protected open class FregePsiElementCellRenderer : DefaultPsiElementCellRenderer() {
        override fun getContainerText(element: PsiElement?, name: String?): String? {
            return element?.parentOfType<FregeProgram>()?.qualifiedName
        }

        companion object {
            val INSTANCE = FregePsiElementCellRenderer()
        }
    }

    protected class FregeGotoClassCellRenderer : FregePsiElementCellRenderer() {
        override fun getElementText(element: PsiElement?): String = when (element) {
            is PsiMember -> element.containingClass?.name ?: super.getElementText(element)
            else -> super.getElementText(element)
        }

        companion object {
            val INSTANCE = FregeGotoClassCellRenderer()
        }
    }
}
