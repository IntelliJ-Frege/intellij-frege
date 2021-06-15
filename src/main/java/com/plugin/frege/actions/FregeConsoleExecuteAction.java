package com.plugin.frege.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.editor.ex.EditorEx;
import com.plugin.frege.runConfiguration.FregeConsoleView;
import com.plugin.frege.runConfiguration.FregeConsoleViewMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;



public class FregeConsoleExecuteAction extends AnAction {
//    public FregeConsoleExecuteAction() {
//        super(new EditorWriteActionHandler() {
//            @Override
//            public void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
//                FregeConsoleView consoleView = FregeConsoleViewMap.getConsole(editor);
//                consoleView.execute();
//            }
//
//            @Override
//            public void executeWriteAction(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
//                getEnterHandler().execute(editor, caret, dataContext);
////                super.executeWriteAction(editor, caret, dataContext);
////                Editor editor = e.getData(CommonDataKeys.EDITOR);
//                FregeConsoleView consoleView = FregeConsoleViewMap.getConsole(editor);
////                if (consoleView) throw new RuntimeException("")
//                // TODO npe
//                consoleView.execute();
//            }
//
//            @Override
//            protected boolean isEnabledForCaret(@NotNull Editor editor, @NotNull Caret caret, DataContext dataContext) {
////                return true;
//                if (!(editor instanceof EditorEx) || ((EditorEx) editor).isRendererMode()) {
//                    return false;
//                } else {
//                    FregeConsoleView consoleView = FregeConsoleViewMap.getConsole(editor);
//                    if (consoleView == null) {
//                        return false;
////                        presentation.setEnabled(false);
//                    } else {
//                        return consoleView.isRunning();
////                        presentation.setEnabledAndVisible(consoleView.isRunning());
//                    }
//                }
////                return super.isEnabledForCaret(editor, caret, dataContext);
//            }
//
//            private EditorActionHandler getEnterHandler() {
//                return EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_ENTER);
//            }
//        });
//    }

//    public FregeConsoleExecuteAction() {
////        setShortcutSet(CustomShortcutSet.fromString("ctrl ENTER"));
//    }

    @Override
    public void update(@NotNull AnActionEvent e) {
//        ActionManager.getInstance()

        Presentation presentation = e.getPresentation();
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (!(editor instanceof EditorEx) || ((EditorEx) editor).isRendererMode()) {
            presentation.setEnabled(false);
        } else {
            FregeConsoleView consoleView = FregeConsoleViewMap.getConsole(editor);
            if (consoleView == null) {
                presentation.setEnabled(false);
            } else {
                presentation.setEnabledAndVisible(consoleView.isRunning());
            }
        }
//        System.err.println("Console execution enabled: " + presentation.isEnabled());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        FregeConsoleView consoleView = FregeConsoleViewMap.getConsole(editor);
        consoleView.execute();
    }
}

//                if (consoleView.isRunning()) {
//////                    AnAction action = ActionManager.getInstance().getAction("Frege.ConsoleExecute");
////                    AnAction editorEnter = ActionManager.getInstance().getAction("EditorEnter");
//////                    editorEnter.isEnabledInModalContext()
////                    // TODO set custom for ENTER
////
//
////                    ShortcutSet ctrlEnterShortcutSet = new CustomShortcutSet(new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), null));
////                    this.registerCustomShortcutSet(ctrlEnterShortcutSet, editor.getComponent());
////                    ShortcutSet enterShortcutSet = new CustomShortcutSet(new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), null));
////                    editorEnter.registerCustomShortcutSet(ctrlEnterShortcutSet, editor.getComponent());
//                }

//        if (editor != null) {
//                if (presentation.isEnabled()) {
////                ShortcutSet ctrlE nterShortcutSet = new CustomShortcutSet(new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), null));
////                this.registerCustomShortcutSet(ctrlEnterShortcutSet, editor.getComponent());
//                } else {
//                this.unregisterCustomShortcutSet(editor.getComponent());
//                }
//                }