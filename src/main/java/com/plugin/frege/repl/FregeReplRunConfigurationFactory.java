package com.plugin.frege.repl;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class FregeReplRunConfigurationFactory extends ConfigurationFactory {
    private static final String FACTORY_NAME = "Frege configuration factory";

    public FregeReplRunConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @Override
    public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new FregeReplRunConfiguration(project, this, "Frege");
    }

    @Override
    public @NotNull
    @NonNls String getId() {
        return "FregeReplRunConfigurationFactory";
    }

    @Override
    public @NotNull String getName() {
        return FACTORY_NAME;
    }
}
