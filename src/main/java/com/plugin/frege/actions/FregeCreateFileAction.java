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

    private static final String CAPTION = "Frege File";

    private static final String FREGE_MODULE = "Frege Module";
    private static final String FREGE_MODULE_TEMPLATE_NAME = "Frege Module";

    private static final String EMPTY_FILE = "Empty File";
    public static final String EMPTY_FILE_TEMPLATE_NAME = "Frege Empty File";

    public FregeCreateFileAction() {
        super(CAPTION, "", FregeIcons.FILE);
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.@NotNull Builder builder) {
        builder.setTitle(CAPTION).addKind(FREGE_MODULE, FregeIcons.FILE, FREGE_MODULE_TEMPLATE_NAME)
                .addKind(EMPTY_FILE, FregeIcons.FILE, EMPTY_FILE_TEMPLATE_NAME);
    }

    @Override
    protected @NlsContexts.Command
    String getActionName(PsiDirectory directory, @NonNls @NotNull String newName, @NonNls String templateName) {
        return CAPTION;
    }
}
