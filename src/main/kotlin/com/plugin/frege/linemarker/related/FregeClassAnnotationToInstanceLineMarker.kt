package com.plugin.frege.linemarker.related

import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.plugin.frege.psi.FregeAnnotationName
import com.plugin.frege.psi.FregeClassDecl
import com.plugin.frege.psi.FregeInstanceDecl
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.resolve.FregeResolveUtil
import com.plugin.frege.stubs.index.FregeMethodNameIndex
import javax.swing.Icon

class FregeClassAnnotationToInstanceLineMarker : FregeRelatedItemLineMarkerAbstract() {
    override fun getTargets(element: PsiElement): List<PsiElement> {
        val annotationName = element.parent as? FregeAnnotationName ?: return emptyList()
        if (FregeResolveUtil.findContainingFregeClass(annotationName) !is FregeClassDecl) {
            return emptyList()
        }
        return FregeMethodNameIndex.INSTANCE
            .findByName(
                annotationName.text,
                element.project,
                GlobalSearchScope.everythingScope(element.project)
            )
            .filter { it.containingClass is FregeInstanceDecl }
    }

    override val icon: Icon
        get() = AllIcons.Gutter.ImplementedMethod
    override val tooltipText: String
        get() = "Navigate to implementations"
}
