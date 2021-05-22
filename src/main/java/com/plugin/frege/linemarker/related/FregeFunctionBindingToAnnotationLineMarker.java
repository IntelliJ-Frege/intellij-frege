package com.plugin.frege.linemarker.related;

import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.psi.FregeAnnotation;
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
        FregeAnnotation annotation = binding.getAnnotation();
        return annotation != null ? List.of(annotation) : List.of();
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
