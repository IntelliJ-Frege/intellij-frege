package com.plugin.frege.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.plugin.frege.repl.FregeReplView;
import com.plugin.frege.repl.FregeReplViewMap;
import org.jetbrains.annotations.NotNull;


public class FregeReplExecuteAction extends AnAction {
    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (!(editor instanceof EditorEx) || ((EditorEx) editor).isRendererMode()) {
            presentation.setEnabled(false);
        } else {
            FregeReplView consoleView = FregeReplViewMap.getConsole(editor);
            if (consoleView == null) {
                presentation.setEnabled(false);
            } else {
                presentation.setEnabledAndVisible(consoleView.isRunning());
            }
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        FregeReplView consoleView = FregeReplViewMap.getConsole(editor);
        if (consoleView != null) {
            consoleView.execute();
        } else {
            throw new RuntimeException("Text was sent to REPL, but no REPL is launched");
        }
    }
}
