package com.plugin.frege.linemarker.related;

import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.plugin.frege.psi.FregeAnnotationName;
import com.plugin.frege.psi.impl.FregeFunctionNameImpl;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class FregeFunctionBindingToAnnotationLineMarker extends FregeRelatedItemLineMarkerAbstract {
    @Override
    protected @NotNull List<@NotNull PsiElement> getTargets(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        if (!(parent instanceof FregeFunctionNameImpl)) {
            return List.of();
        }
        FregeFunctionNameImpl functionName = (FregeFunctionNameImpl) parent;
        if (!functionName.isFunctionBinding()) {
            return List.of();
        }
        FregeAnnotationName annotationName = functionName.getAnnotationName();
        return annotationName != null ? List.of(annotationName) : List.of();
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
