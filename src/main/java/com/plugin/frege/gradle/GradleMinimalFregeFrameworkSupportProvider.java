package com.plugin.frege.gradle;

import com.intellij.framework.FrameworkTypeEx;
import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider;
import com.intellij.openapi.externalSystem.model.project.ProjectId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.plugin.frege.FregeIcons;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.io.StringSubstitutorReader;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.gradle.frameworkSupport.BuildScriptDataBuilder;
import org.jetbrains.plugins.gradle.frameworkSupport.GradleFrameworkSupportProvider;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.intellij.util.ResourceUtil.getResourceAsStream;

public class GradleMinimalFregeFrameworkSupportProvider extends GradleFrameworkSupportProvider {
    public static final String ID = "Frege Minimal";
    private final GradleMinimalFregeForm settingsForm;

    public GradleMinimalFregeFrameworkSupportProvider() {
        this.settingsForm = new GradleMinimalFregeForm();
    }

    @Override
    public @NotNull FrameworkTypeEx getFrameworkType() {
        return new FrameworkTypeEx(ID) {
            @Override
            public @NotNull FrameworkSupportInModuleProvider createProvider() {
                return GradleMinimalFregeFrameworkSupportProvider.this;
            }

            @Override
            public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getPresentableName() {
                return "Frege Minimal";
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

//    @Override
//    public @NotNull FrameworkSupportInModuleConfigurable createConfigurable(@NotNull FrameworkSupportModel model) {
//        return new FrameworkSupportInModuleConfigurable() {
//            @Override
//            public @Nullable JComponent createComponent() {
//                return settingsForm.getPanel();
//            }
//
//            @Override
//            public void addSupport(@NotNull Module module,
//                                   @NotNull ModifiableRootModel rootModel,
//                                   @NotNull ModifiableModelsProvider modifiableModelsProvider) {
//
//            }
//        };
//    }

    private Map<String, String> getUserSettingsMapping() {
        Map<String, String> mapping = new HashMap<>();

        mapping.put("javaTargetUserSettings", settingsForm.getJavaTarget());
        mapping.put("fregeReleaseUserSettings", settingsForm.getFregeRelease());
        mapping.put("fregeVersionUserSettings", settingsForm.getFregeVersion());

        return mapping;
    }

    private <V> byte[] substituteUserSettings(byte[] contentBytes, Map<String, V> userSettingsMapping) {
//        byte[] contentBytes = getResourceAsStream(GradleMinimalFregeFrameworkSupportProvider.class.getClassLoader(),
//                "templates/gradle/minimal", "build.gradle").readAllBytes();
        String content = new String(contentBytes);
        StringSubstitutor sub = new StringSubstitutor(userSettingsMapping);
        String contentModified = sub.replace(content);
        return contentModified.getBytes(StandardCharsets.UTF_8);

//        StringSubstitutorReader stringSubstitutorReader = new StringSubstitutorReader(buildGradleReader, null);
//        BufferedReader bufferedReader = new BufferedReader(stringSubstitutorReader);
//
//        char[] content = new char[]{};
//        try {
//            stringSubstitutorReader.read(content);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return (byte[]) content;
//        return null;
    }

    @Override
    public void addSupport(@NotNull ProjectId projectId,
                           @NotNull Module module,
                           @NotNull ModifiableRootModel rootModel,
                           @NotNull ModifiableModelsProvider modifiableModelsProvider,
                           @NotNull BuildScriptDataBuilder buildScriptData) {

//        LocalFileSystem.getInstance().findFileByIoFile()
        ContentEntry[] contentEntries = rootModel.getContentEntries();
        for (ContentEntry contentEntry: contentEntries) {
            VirtualFile fregeMainDir;
            try {
                fregeMainDir = VfsUtil.createDirectoryIfMissing(contentEntry.getFile(), "src/main/frege"); // TODO move to bundle
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            contentEntry.addSourceFolder(fregeMainDir, false); // TODO seems, it doesn't work... though addExcludedFolder works...
        }

        try {
            byte[] contentBytes = getResourceAsStream(GradleMinimalFregeFrameworkSupportProvider.class.getClassLoader(),
                    "templates/gradle/minimal", "build.gradle").readAllBytes();

            byte[] preprocessedContent = substituteUserSettings(contentBytes, getUserSettingsMapping());

//            String content = new String(contentBytes, StandardCharsets.UTF_8);

            buildScriptData.getBuildScriptFile().setBinaryContent(preprocessedContent);
//            buildScriptData.addOther("Java Target = " + settingsForm.getJavaTarget());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        buildScriptData.addPropertyDefinition("javaTarget = 1.8");
    }
}
