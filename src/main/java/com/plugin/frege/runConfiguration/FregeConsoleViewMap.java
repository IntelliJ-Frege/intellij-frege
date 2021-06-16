package com.plugin.frege.runConfiguration;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class FregeConsoleViewMap {
    private static final ConcurrentHashMap<Editor, FregeConsoleView> consoleViews = new ConcurrentHashMap<>();

    public static void addConsole(FregeConsoleView console) {
        consoleViews.put(console.getConsoleEditor(), console);
    }

    public static void delConsole(FregeConsoleView console) {
        consoleViews.remove(console.getConsoleEditor());
    }

    @Nullable
    public static FregeConsoleView getConsole(Project project) {
        return consoleViews.values().stream().filter(c -> c.getConsoleProject() == project && c.isShowing()).findFirst().orElse(null);
    }

    @Nullable
    public static FregeConsoleView getConsole(Editor editor) {
        return consoleViews.get(editor);
    }
}
