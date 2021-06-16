package com.plugin.frege.repl;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class FregeReplViewMap {
    private static final ConcurrentHashMap<Editor, FregeReplView> consoleViews = new ConcurrentHashMap<>();

    public static void addConsole(FregeReplView console) {
        consoleViews.put(console.getConsoleEditor(), console);
    }

    public static void delConsole(FregeReplView console) {
        consoleViews.remove(console.getConsoleEditor());
    }

    @Nullable
    public static FregeReplView getConsole(Project project) {
        return consoleViews.values().stream().filter(c -> c.getConsoleProject() == project && c.isShowing()).findFirst().orElse(null);
    }

    @Nullable
    public static FregeReplView getConsole(Editor editor) {
        return consoleViews.get(editor);
    }
}
