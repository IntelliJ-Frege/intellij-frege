package com.plugin.frege.repl;

import com.intellij.configurationStore.XmlSerializer;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FregeReplRunConfiguration extends ModuleBasedConfiguration<RunConfigurationModule, FregeReplRunConfiguration> {
    private final Project project;
    private final FregeReplConfigurationEditor editor;

    static class FregeRunConfigurationBean {
        public boolean moduleSelected = false;
        public @NotNull String selectedModuleName = "";
        public @NotNull String additionalArguments = "";
        public @NotNull List<@NotNull String> modulesToLoad = new ArrayList<>();
    }

    FregeRunConfigurationBean myBean = new FregeRunConfigurationBean();


    protected FregeReplRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(name, new RunConfigurationModule(project), factory);
        this.project = project;
        editor = new FregeReplConfigurationEditor(project);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return editor;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (!myBean.moduleSelected) {
            throw new RuntimeConfigurationException("No module selected");
        }
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return new FregeReplState(this, executionEnvironment);
    }

    @Override
    public Collection<Module> getValidModules() {
        return Arrays.stream(ModuleManager.getInstance(project).getModules()).collect(Collectors.toList());
    }

    public @Nullable String getSelectedModuleName() {
        return myBean.moduleSelected ? myBean.selectedModuleName : null;
    }

    public void setSelectedModuleName(@Nullable String selectedModuleName) {
        if (selectedModuleName == null) {
            this.myBean.moduleSelected = false;
            this.myBean.selectedModuleName = "";
        } else {
            this.myBean.moduleSelected = true;
            this.myBean.selectedModuleName = selectedModuleName;
        }
    }

    public @NotNull String getAdditionalArguments() {
        return myBean.additionalArguments;
    }

    public void setAdditionalArguments(@NotNull String additionalArguments) {
        this.myBean.additionalArguments = additionalArguments;
    }

    public List<String> getModulesToLoad() {
        return myBean.modulesToLoad;
    }

    public void setModulesToLoad(List<String> modulesToLoad) {
        this.myBean.modulesToLoad = modulesToLoad;
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);

            XmlSerializer.deserializeInto(element, myBean);

        if (myBean.moduleSelected) {
            editor.setModule(myBean.selectedModuleName);
        } else {
            editor.setModule(null);
        }
        editor.setArguments(myBean.additionalArguments);
        editor.setModulesToLoad(project, myBean.modulesToLoad);
    }

    @Override
    public void writeExternal(@NotNull Element element) throws WriteExternalException {
        super.writeExternal(element);
        XmlSerializer.serializeObjectInto(myBean, element);
    }
}
