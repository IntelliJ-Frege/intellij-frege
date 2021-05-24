package com.plugin.frege.linemarker.related;

import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.psi.FregeAnnotationItem;
import com.plugin.frege.psi.impl.FregeBindingImpl;
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
        FregeBindingImpl binding = PsiTreeUtil.getParentOfType(parent, FregeBindingImpl.class);
        if (binding == null) {
            return List.of();
        }
        FregeAnnotationItem annotationItem = binding.getAnnoItem();
        return annotationItem != null ? List.of(annotationItem) : List.of();
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
