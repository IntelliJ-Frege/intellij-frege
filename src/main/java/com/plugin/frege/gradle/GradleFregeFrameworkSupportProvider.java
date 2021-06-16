package com.plugin.frege.gradle;

import com.intellij.framework.FrameworkTypeEx;
import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider;
import com.intellij.openapi.externalSystem.model.project.ProjectId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.plugin.frege.FregeIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.gradle.frameworkSupport.BuildScriptDataBuilder;
import org.jetbrains.plugins.gradle.frameworkSupport.GradleFrameworkSupportProvider;

import javax.swing.*;
import java.io.IOException;

import static com.intellij.util.ResourceUtil.getResourceAsStream;

public class GradleFregeFrameworkSupportProvider extends GradleFrameworkSupportProvider {
    private static final String ID = "Frege";
    private final GradleFregeForm settingsForm = new GradleFregeForm();

    public GradleFregeFrameworkSupportProvider() {
    }

    @Override
    public @NotNull FrameworkTypeEx getFrameworkType() {
        return new FrameworkTypeEx(ID) {
            @Override
            public @NotNull FrameworkSupportInModuleProvider createProvider() {
                return GradleFregeFrameworkSupportProvider.this;
            }

            @Override
            public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getPresentableName() {
                return ID;
            }

            @Override
            public @NotNull Icon getIcon() {
                return FregeIcons.FILE;
            }
        };
    }

    @Override
    public JComponent createComponent() {
        return settingsForm.getPanel();
    }


    @Override
    public void addSupport(@NotNull ProjectId projectId,
                           @NotNull Module module,
                           @NotNull ModifiableRootModel rootModel,
                           @NotNull ModifiableModelsProvider modifiableModelsProvider,
                           @NotNull BuildScriptDataBuilder buildScriptData) {
        try {
            byte[] contentBytes = getResourceAsStream(this.getClass().getClassLoader(),
                    "templates/gradle/minimal", "build.gradle").readAllBytes();

            VirtualFile gradlePropertiesFile = GradleFregePropertiesUtils.createGradlePropertiesFile(module);
            GradleFregePropertiesUtils.writeSettingsToGradlePropertiesFile(gradlePropertiesFile, settingsForm);

            buildScriptData.getBuildScriptFile().setBinaryContent(contentBytes);
        } catch (IOException | GradleFregeException e) {
            throw new RuntimeException(e);
        }
    }
}
