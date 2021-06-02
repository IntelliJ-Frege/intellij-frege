package com.plugin.frege.linemarker.related

import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.plugin.frege.psi.*
import com.plugin.frege.psi.impl.FregeBindingImpl
import com.plugin.frege.psi.impl.FregeInstanceDeclImpl
import javax.swing.Icon

class FregeInstanceMethodToClassLineMarker : FregeRelatedItemLineMarkerAbstract() {
    override fun getTargets(element: PsiElement): List<PsiElement> {
        val nameElement = getNameElement(element) ?: return emptyList()
        val instanceClass = nameElement.parentOfType<FregePsiClass>() as? FregeInstanceDeclImpl
            ?: return emptyList()
        val classDecl = instanceClass.getInstancedClass() ?: return emptyList()
        return classDecl.findMethodsByName(nameElement.text, false).toList()
    }

    private fun getNameElement(element: PsiElement): PsiElement? {
        when (val parent = element.parent) {
            is FregeFunctionName, is FregeSymbolOperator -> {
                val binding = parent.parentOfType<FregeBindingImpl>() ?: return null
                return if (binding.getAnnoItem() == null) parent else null
            }
            is FregeAnnotationName -> {
                return parent
            }
            else -> {
                return null
            }
        }
    }

    override fun getIcon(): Icon {
        return AllIcons.Gutter.ImplementingMethod
    }

    override fun getTooltipText(): String {
        return "Navigate to instanced method"
    }
}
