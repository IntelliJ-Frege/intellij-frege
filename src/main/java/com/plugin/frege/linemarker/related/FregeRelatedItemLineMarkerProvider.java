package com.plugin.frege.linemarker.related;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class FregeRelatedItemLineMarkerProvider extends RelatedItemLineMarkerProvider {
    FregeFunctionBindingToAnnotationLineMarker functionBindingToAnnotation = new FregeFunctionBindingToAnnotationLineMarker();
    FregeNativeFunctionToDelegatedMemberLineMarker nativeFunctionToDelegatedMember = new FregeNativeFunctionToDelegatedMemberLineMarker();

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        functionBindingToAnnotation.addMarker(element, result);
        nativeFunctionToDelegatedMember.addMarker(element, result);
    }
}
