package com.plugin.frege.linemarker.related;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

public abstract class FregeRelatedItemLineMarkerAbstract {

    public void addMarker(@NotNull PsiElement element,
                          @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        List<PsiElement> targets = getTargets(element);
        if (targets.isEmpty()) {
            return;
        }
        RelatedItemLineMarkerInfo<PsiElement> markerInfo =
                NavigationGutterIconBuilder.create(getIcon())
                        .setTargets(getTargets(element))
                        .setTooltipText(getTooltipText()).createLineMarkerInfo(element);
        result.add(markerInfo);
    }

    protected abstract @NotNull List<@NotNull PsiElement> getTargets(@NotNull PsiElement element);

    protected abstract @NotNull Icon getIcon();

    protected abstract @NotNull String getTooltipText();
}
