package com.plugin.frege.resolve;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.plugin.frege.psi.FregeFunctionName;
import org.jetbrains.annotations.NotNull;

public class FregeReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(FregeFunctionName.class),
                FregeFunctionNameReference.getReferenceProvider());
    }
}
