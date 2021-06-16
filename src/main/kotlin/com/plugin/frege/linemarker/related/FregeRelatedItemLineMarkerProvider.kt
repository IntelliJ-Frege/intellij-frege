package com.plugin.frege.linemarker.related

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiElement

class FregeRelatedItemLineMarkerProvider : RelatedItemLineMarkerProvider() {
    private val functionBindingToAnnotation = FregeFunctionBindingToAnnotationLineMarker()
    private val nativeFunctionToDelegatedMember = FregeNativeFunctionToDelegatedMemberLineMarker()
    private val instanceMethodToClass = FregeInstanceMethodToClassLineMarker()
    private val classAnnotationToInstance = FregeClassAnnotationToInstanceLineMarker()
    private val classBindingToInstance = FregeClassBindingToInstanceLineMarker()

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        functionBindingToAnnotation.addMarker(element, result)
        nativeFunctionToDelegatedMember.addMarker(element, result)
        instanceMethodToClass.addMarker(element, result)
        classAnnotationToInstance.addMarker(element, result)
        classBindingToInstance.addMarker(element, result)
    }
}
