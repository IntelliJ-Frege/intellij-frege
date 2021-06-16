package com.plugin.frege.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.plugin.frege.repl.FregeReplView;
import com.plugin.frege.repl.FregeReplViewMap;
import org.jetbrains.annotations.NotNull;

public class FregeReplSendToConsole extends AnAction {
    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(FregeReplViewMap.getConsole(e.getProject()) != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DataContext context = e.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(context);
        if (editor == null) return;
        Project project = CommonDataKeys.PROJECT.getData(context);
        if (project == null) return;
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (selectedText == null) return; // TODO get scope instead?
        FregeReplView console = FregeReplViewMap.getConsole(project);
        if (console == null) return; // TODO log or create console
        console.executeCommand(selectedText);
    }
}
