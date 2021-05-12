package com.plugin.frege.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.PsiDirectory;
import com.plugin.frege.FregeIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class FregeCreateFileAction extends CreateFileFromTemplateAction implements DumbAware {

    public static final String CAPTION = "Frege File";

    public FregeCreateFileAction() {
        super(CAPTION, "", FregeIcons.FILE);
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.@NotNull Builder builder) {
        builder.setTitle(CAPTION).addKind("Frege Module", FregeIcons.FILE, "Frege Module")
                .addKind("Empty File", FregeIcons.FILE, "Frege Empty File");
    }

    @Override
    protected @NlsContexts.Command
    String getActionName(PsiDirectory directory, @NonNls @NotNull String newName, @NonNls String templateName) {
        return CAPTION;
    }
}
