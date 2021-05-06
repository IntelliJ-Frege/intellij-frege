package com.plugin.frege.linemarker.related;

import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import com.plugin.frege.psi.FregeFunctionName;
import com.plugin.frege.psi.impl.FregeFunctionNameImpl;
import com.plugin.frege.psi.mixin.FregeFunctionNameMixin;
import com.plugin.frege.resolve.FregeFunctionNameReference;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FregeFunctionBindingToAnnotationLineMarker extends FregeRelatedItemLineMarkerAbstract {
    @Override
    protected @NotNull List<@NotNull PsiElement> getTargets(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        if (!(parent instanceof FregeFunctionName)) {
            return Collections.emptyList();
        }
        FregeFunctionNameImpl functionName = (FregeFunctionNameImpl) parent;
        if (!functionName.isFunctionBinding()) {
            return Collections.emptyList();
        }
        FregeFunctionNameReference functionNameReference = (FregeFunctionNameReference) functionName.getReference();
        if (functionNameReference == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(functionNameReference.multiResolve(false))
                .map(ResolveResult::getElement)
                .filter(FregeFunctionNameImpl.class::isInstance)
                .map(FregeFunctionNameImpl.class::cast)
                .filter(FregeFunctionNameMixin::isFunctionAnnotation)
                .collect(Collectors.toList());
    }

    @Override
    protected @NotNull Icon getIcon() {
        return AllIcons.Gutter.ImplementingMethod;
    }

    @Override
    protected @NotNull String getTooltipText() {
        return "Navigate to type annotation";
    }
}
