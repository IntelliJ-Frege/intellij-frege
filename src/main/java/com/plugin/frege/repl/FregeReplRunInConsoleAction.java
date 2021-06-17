package com.plugin.frege.repl;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class FregeReplRunInConsoleAction extends AnAction {
    @Override
    public void update(@NotNull AnActionEvent e) {
        DataContext context = e.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(context);
        if (editor == null) {
            e.getPresentation().setEnabled(false);
            return;
        }
        e.getPresentation().setEnabled(editor.getSelectionModel().hasSelection());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DataContext context = e.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(context);
        if (editor == null) return;
        Project project = CommonDataKeys.PROJECT.getData(context);
        if (project == null) return;
        FregeReplRunInConsole.invokeRunInConsole(project, editor);
    }
}
