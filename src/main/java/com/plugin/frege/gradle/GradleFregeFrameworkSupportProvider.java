package com.plugin.frege.gradle;

import com.intellij.framework.FrameworkTypeEx;
import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider;
import com.intellij.openapi.externalSystem.model.project.ProjectId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.plugin.frege.FregeIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.gradle.frameworkSupport.BuildScriptDataBuilder;
import org.jetbrains.plugins.gradle.frameworkSupport.GradleFrameworkSupportProvider;

import javax.swing.*;
import java.io.IOException;

import static com.intellij.util.ResourceUtil.getResourceAsStream;

public class GradleFregeFrameworkSupportProvider extends GradleFrameworkSupportProvider {
    public static final String ID = "Frege";

    @Override
    public @NotNull FrameworkTypeEx getFrameworkType() {
        return new FrameworkTypeEx(ID) {
            @Override
            public @NotNull FrameworkSupportInModuleProvider createProvider() {
                return GradleFregeFrameworkSupportProvider.this;
            }

            @Override
            public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getPresentableName() {
                return "Frege";
            }

            @Override
            public @NotNull Icon getIcon() {
                return FregeIcons.FILE;
            }
        };
    }

    @Override
    public void addSupport(@NotNull ProjectId projectId,
                           @NotNull Module module,
                           @NotNull ModifiableRootModel rootModel,
                           @NotNull ModifiableModelsProvider modifiableModelsProvider,
                           @NotNull BuildScriptDataBuilder buildScriptData) {

        try {
            byte[] content = getResourceAsStream(GradleFregeFrameworkSupportProvider.class.getClassLoader(),
                    "templates/gradle", "build.gradle").readAllBytes();
            buildScriptData.getBuildScriptFile().setBinaryContent(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}