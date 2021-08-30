package com.plugin.frege.linemarker.related

import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.FregeClassDecl
import com.plugin.frege.psi.FregeFunctionName
import com.plugin.frege.psi.FregeInstanceDecl
import com.plugin.frege.psi.FregeSymbolOperator
import com.plugin.frege.psi.impl.FregeBindingImpl
import com.plugin.frege.stubs.index.FregeMethodNameIndex

class FregeClassBindingToInstanceLineMarker : FregeRelatedItemLineMarkerAbstract() {
    override val icon get() = AllIcons.Gutter.OverridenMethod

    override val tooltipText get() = "Navigate to overrides"

    override val cellRenderer get() = FregeGotoClassCellRenderer.INSTANCE

    override fun getTargets(element: PsiElement): List<PsiElement> {
        val parent = element.parent
        if (parent !is FregeFunctionName && parent !is FregeSymbolOperator) {
            return emptyList()
        }
        val binding = parent.parentOfType<FregeBindingImpl>() ?: return emptyList()
        if (binding.containingClass !is FregeClassDecl) {
            return emptyList()
        }
        return FregeMethodNameIndex.findByName(
            binding.name,
            element.project,
            GlobalSearchScope.everythingScope(element.project)
        ).filter { it.containingClass is FregeInstanceDecl }
    }
}
