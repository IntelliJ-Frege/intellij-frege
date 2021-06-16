package com.plugin.frege.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import com.plugin.frege.runConfiguration.FregeConsoleView;
import com.plugin.frege.runConfiguration.FregeConsoleViewMap;
import org.jetbrains.annotations.NotNull;

public class FregeReplSendToConsole extends AnAction {
    @Override
    public void update(@NotNull AnActionEvent e) {
        System.err.println("Repl update is called");
        e.getPresentation().setEnabled(FregeConsoleViewMap.getConsole(e.getProject()) != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.err.println("Repl performed is called");
        DataContext context = e.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(context);
        if (editor == null) return;
        Project project = CommonDataKeys.PROJECT.getData(context);
        if (project == null) return;
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (selectedText == null) return; // TODO no text selected, mb get scope or smth like it?
        FregeConsoleView console = FregeConsoleViewMap.getConsole(project);
        if (console == null) return; // TODO log or create console
        console.executeCommand(selectedText);
    }
}
