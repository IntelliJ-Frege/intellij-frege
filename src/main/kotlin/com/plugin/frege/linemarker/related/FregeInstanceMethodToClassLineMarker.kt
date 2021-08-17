package com.plugin.frege.linemarker.related

import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.FregeAnnotationName
import com.plugin.frege.psi.FregeFunctionName
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.FregeSymbolOperator
import com.plugin.frege.psi.impl.FregeBindingImpl
import com.plugin.frege.psi.impl.FregeInstanceDeclImpl

class FregeInstanceMethodToClassLineMarker : FregeRelatedItemLineMarkerAbstract() {
    override val icon get() = AllIcons.Gutter.ImplementingMethod

    override val tooltipText get() = "Navigate to instanced method"

    override val cellRenderer get() = FregeGotoClassCellRenderer.INSTANCE

    override fun getTargets(element: PsiElement): List<PsiElement> {
        val nameElement = getNameElement(element) ?: return emptyList()
        val instanceClass = nameElement.parentOfType<FregePsiClass>() as? FregeInstanceDeclImpl ?: return emptyList()
        val classDecl = instanceClass.getInstancedClass() ?: return emptyList()
        return classDecl.findMethodsByName(nameElement.text, false).toList()
    }

    private fun getNameElement(element: PsiElement): PsiElement? {
        return when (val parent = element.parent) {
            is FregeFunctionName, is FregeSymbolOperator -> {
                val binding = parent.parentOfType<FregeBindingImpl>() ?: return null
                if (binding.getAnnoItem() == null) parent else null
            }
            is FregeAnnotationName -> parent
            else -> null
        }
    }
}
