package com.plugin.frege.repl;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.util.Consumer;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class FregeReplRunInConsole {
    public static void invokeRunInConsole(@NotNull Project project, @NotNull Editor editor) {
        String textToRun = editor.getSelectionModel().getSelectedText();
        if (textToRun == null) {
            throw new IncorrectOperationException("No text selected");
        }

        invokeRunInConsoleWithTextToRun(project, editor, textToRun);
    }

    public static void invokeRunInConsoleWithTextToRun(@NotNull Project project, @NotNull Editor editor, @NotNull String textToRun) {
        List<FregeReplView> fregeLaunchedREPLs = FregeReplViewMap.getConsoles(project);
        if (fregeLaunchedREPLs.isEmpty()) {
            showPromptByEnum(REPLPromptEnum.CREATE_NEW_REPL, project, editor, textToRun);
        } else {
            showPromptChooser(project, editor, textToRun);
        }
    }

    private static void showPromptChooser(@NotNull Project project, @NotNull Editor editor, @NotNull String textToRun) {
        List<REPLPromptEnum> REPLPromptsList = List.of(REPLPromptEnum.values());
        JBPopup popup = JBPopupFactory.getInstance().createPopupChooserBuilder(REPLPromptsList)
                .setTitle("Choose REPL Action")
                .setItemChosenCallback(chosenPrompt -> showPromptByEnum(chosenPrompt, project, editor, textToRun))
                .setNamerForFiltering(Object::toString)
                .setRenderer(new REPLPromptEnumRenderer())
                .createPopup();
        showPromptForChoosingLaunchedREPL(project, editor, textToRun);
        popup.showInBestPositionFor(editor);
    }

    private static void showPromptByEnum(REPLPromptEnum chosen, @NotNull Project project, @NotNull Editor editor, @NotNull String textToRun) {
        switch (chosen) {
            case RUN_IN_EXISTING_REPL:
                showPromptForChoosingLaunchedREPL(project, editor, textToRun);
                break;
            case CREATE_NEW_REPL:
                showPromptForChoosingREPLRunConfiguration(project, editor, textToRun);
                break;
        }
    }

    private static void showPromptForChoosingLaunchedREPL(@NotNull Project project, @NotNull Editor editor, @NotNull String textToRun) {
        List<FregeReplView> fregeLaunchedREPLs = FregeReplViewMap.getConsoles();
        JBPopup popup = JBPopupFactory.getInstance().createPopupChooserBuilder(fregeLaunchedREPLs)
                .setTitle("Choose Which REPL to Run Text in")
                .setItemChosenCallback(repl -> repl.executeCommand(textToRun))
                .setNamerForFiltering(FregeReplView::getName)
                .setRenderer(new FregeREPLRenderer())
                .createPopup();
        NavigationUtil.hidePopupIfDumbModeStarts(popup, project);
        popup.showInBestPositionFor(editor);
    }

    private static void showPromptForChoosingREPLRunConfiguration(@NotNull Project project, @NotNull Editor editor, @NotNull String textToRun) {
        RunManager manager = RunManager.getInstance(project);
        List<RunnerAndConfigurationSettings> fregeReplConfigurations = manager.getConfigurationSettingsList(FregeReplRunConfigurationType.class);
        JBPopup popup = JBPopupFactory.getInstance().createPopupChooserBuilder(fregeReplConfigurations)
                .setTitle("Choose REPL Configuration to Start New Console")
                .setItemChosenCallback(getRunnerAndConfigurationSettingsConsumer(textToRun))
                .setNamerForFiltering(RunnerAndConfigurationSettings::getName)
                .setRenderer(new RunConfigurationRenderer())
                .createPopup();
        NavigationUtil.hidePopupIfDumbModeStarts(popup, project);
        popup.showInBestPositionFor(editor);
    }

    @NotNull
    private static Consumer<RunnerAndConfigurationSettings> getRunnerAndConfigurationSettingsConsumer(String textToRun) {
        return config -> {
            FregeReplViewMap.addConsoleAdditionListener(getSelfRemovingTextRunningListener(textToRun));
            ProgramRunnerUtil.executeConfiguration(config, DefaultRunExecutor.getRunExecutorInstance());
        };
    }

    @NotNull
    private static Consumer<FregeReplView> getSelfRemovingTextRunningListener(String textToRun) {
        return new Consumer<>() {
            @Override
            public void consume(FregeReplView console) {
                console.executeCommand(textToRun);
                FregeReplViewMap.removeConsoleAdditionListener(this);
            }
        };
    }

    private enum REPLPromptEnum {
        RUN_IN_EXISTING_REPL("Run in existing REPL"),
        CREATE_NEW_REPL("Create new REPL and run text in it");

        private final String meaning;

        REPLPromptEnum(String s) {
            this.meaning = s;
        }

        @Override
        public String toString() {
            return meaning;
        }
    }

    private static class FregeREPLRenderer extends ColoredListCellRenderer<FregeReplView> {
        @Override
        protected void customizeCellRenderer(@NotNull JList<? extends FregeReplView> list,
                                             FregeReplView value,
                                             int index,
                                             boolean selected,
                                             boolean hasFocus) {

            append(value.getTitle());
        }
    }

    private static class RunConfigurationRenderer extends ColoredListCellRenderer<RunnerAndConfigurationSettings> {
        @Override
        protected void customizeCellRenderer(@NotNull JList<? extends RunnerAndConfigurationSettings> list,
                                             RunnerAndConfigurationSettings value,
                                             int index,
                                             boolean selected,
                                             boolean hasFocus) {
            append(value.getName());
        }
    }

    private static class REPLPromptEnumRenderer extends ColoredListCellRenderer<REPLPromptEnum> {
        @Override
        protected void customizeCellRenderer(@NotNull JList<? extends REPLPromptEnum> list,
                                             REPLPromptEnum value,
                                             int index,
                                             boolean selected,
                                             boolean hasFocus) {
            append(value.toString());
        }
    }
}
