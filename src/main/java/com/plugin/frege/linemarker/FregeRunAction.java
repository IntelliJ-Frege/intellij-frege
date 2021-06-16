package com.plugin.frege.linemarker;

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.gradle.service.execution.GradleExternalTaskConfigurationType;
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration;

import java.util.List;

public class FregeRunAction extends AnAction {
    private final String moduleName;

    // default constructor is needed because action class must have a no-argument constructor
    FregeRunAction() {
        super("Run", "Run this class `main` function", AllIcons.RunConfigurations.TestState.Run);
        this.moduleName = "";
    }

    FregeRunAction(String moduleName) {
        super("Run " + moduleName, "Run this class `main` function", AllIcons.RunConfigurations.TestState.Run);
        this.moduleName = moduleName;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getProject() == null)
            throw new IllegalStateException("Project must be not null");
        RunManager manager = RunManager.getInstance(e.getProject());

        String configurationName = moduleName.equals("") ? "Run" : String.format("Run [%s]", moduleName);
        RunnerAndConfigurationSettings newSettings = manager.createConfiguration(configurationName, GradleExternalTaskConfigurationType.getInstance().getFactory());
        GradleRunConfiguration newGradleConfiguration = (GradleRunConfiguration) newSettings.getConfiguration();

        ExternalSystemTaskExecutionSettings gradleSettings = newGradleConfiguration.getSettings();
        gradleSettings.setExternalProjectPath(newGradleConfiguration.getProject().getBasePath());
        gradleSettings.setTaskNames(List.of("fregeRun"));
        gradleSettings.setVmOptions("");
        gradleSettings.setScriptParameters("-Pclass_name=" + moduleName);
        newSettings.setTemporary(true);

        manager.addConfiguration(newSettings);
        manager.setSelectedConfiguration(newSettings);

        ProgramRunnerUtil.executeConfiguration(newSettings, DefaultRunExecutor.getRunExecutorInstance());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }
}
