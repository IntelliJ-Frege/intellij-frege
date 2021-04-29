package com.plugin.frege.findusages;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.plugin.frege.psi.FregeFunctionName;
import com.plugin.frege.psi.FregeNamedElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FregeFindUsagesProvider implements FindUsagesProvider {
    @Override
    public @Nullable WordsScanner getWordsScanner() {
        return null;
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement element) {
        return element instanceof FregeNamedElement;
    }

    @Override
    public @Nullable
    @NonNls String getHelpId(@NotNull PsiElement element) {
        return null;
    }

    @Override
    public @Nls @NotNull String getType(@NotNull PsiElement element) {
        if (element instanceof FregeFunctionName) {
            return "frege function name";
        } else {
            return "";
        }
    }

    @Override
    public @Nls @NotNull String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof FregeFunctionName) {
            return element.getText();
        } else {
            return "";
        }
    }

    @Override
    public @Nls @NotNull String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof FregeFunctionName) {
            return element.getText();
        } else {
            return "";
        }
    }
}
