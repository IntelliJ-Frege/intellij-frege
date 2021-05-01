package com.plugin.frege.linemarker;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration;

// TODO
// not implemented yet
public class FregeRunConfigurationProducer extends RunConfigurationProducer<GradleRunConfiguration> {
    public FregeRunConfigurationProducer(boolean internalUsageOnly) {
        super(internalUsageOnly);
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull GradleRunConfiguration configuration, @NotNull ConfigurationContext context, @NotNull Ref<PsiElement> sourceElement) {
        return false;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull GradleRunConfiguration configuration, @NotNull ConfigurationContext context) {
        return false;
    }

}
