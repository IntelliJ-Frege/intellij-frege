package com.plugin.frege.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.actions.AttributesDefaults;
import com.intellij.ide.fileTemplates.ui.CreateFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.plugin.frege.FregeIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

public class FregeCreateFileAction extends CreateFileFromTemplateAction implements DumbAware {

    public static final String CAPTION = "Frege File";

    public FregeCreateFileAction() {
        super(CAPTION, "", FregeIcons.FILE);
    }

    @Override
    protected void buildDialog  (@NotNull Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.@NotNull Builder builder) {
        builder.setTitle(CAPTION).addKind("Frege Module", FregeIcons.FILE, "Frege Module")
                .addKind("Empty File", FregeIcons.FILE, "Frege Empty File");
    }

    @Override
    protected @NlsContexts.Command
    String getActionName(PsiDirectory directory, @NonNls @NotNull String newName, @NonNls String templateName) {
        return CAPTION;
    }

//    @Override
//    protected PsiFile createFileFromTemplate(String name, FileTemplate template, PsiDirectory dir) {
//        String className = FileUtilRt.getNameWithoutExtension(name);
//        Project project = dir.getProject();
//        Properties properties = FileTemplateManager.getInstance(project).getDefaultProperties();
//        properties.put("NAME", className);
//        CreateFromTemplateDialog createFromTemplateDialog = new CreateFromTemplateDialog(project, dir, template, new AttributesDefaults(className).withFixedName(true), properties);
//        return createFromTemplateDialog.create().getContainingFile();
////        new CreateFileFromTemplateDialog(project, dir, template);
////        dir.getProject().getBasePath();
////        return super.createFileFromTemplate(name, template, dir);
//    }


}
