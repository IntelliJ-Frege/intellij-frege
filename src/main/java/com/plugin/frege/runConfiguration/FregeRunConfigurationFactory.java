package com.plugin.frege.runConfiguration;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;

public class FregeRunConfigurationFactory extends ConfigurationFactory {
    private static final String FACTORY_NAME = "Frege configuration factory";

    public FregeRunConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @Override
    public RunConfiguration createTemplateConfiguration(Project project) {
        return new FregeRunConfiguration(project, this, "Frege");
    }

    @Override
    public String getName() {
        return FACTORY_NAME;
    }
}