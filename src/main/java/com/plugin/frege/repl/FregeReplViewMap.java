package com.plugin.frege.repl;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FregeReplViewMap {
    private static final ConcurrentLinkedDeque<Consumer<FregeReplView>> consoleAdditionListeners = new ConcurrentLinkedDeque<>();
    private static final ConcurrentLinkedDeque<Consumer<FregeReplView>> consoleRemovalListeners = new ConcurrentLinkedDeque<>();

    private static final ConcurrentHashMap<Editor, FregeReplView> consoleViews = new ConcurrentHashMap<>();

    public static void addConsole(FregeReplView console) {
        consoleViews.put(console.getConsoleEditor(), console);
        consoleAdditionListeners.forEach(c -> c.consume(console));
    }

    public static void removeConsole(FregeReplView console) {
        consoleViews.remove(console.getConsoleEditor());
        consoleRemovalListeners.forEach(c -> c.consume(console));
    }

    @Nullable
    public static FregeReplView getConsole(Project project) {
        return getConsolesByProjectStream(project).findFirst().orElse(null);
    }

    @Nullable
    public static FregeReplView getConsole(Editor editor) {
        return consoleViews.get(editor);
    }

    @NotNull
    public static List<FregeReplView> getConsoles() {
        return new ArrayList<>(consoleViews.values());
    }

    @NotNull
    public static List<FregeReplView> getConsoles(Project project) {
        return getConsolesByProjectStream(project).collect(Collectors.toList());
    }

    @NotNull
    private static Stream<FregeReplView> getConsolesByProjectStream(Project project) {
        return consoleViews.values().stream().filter(c -> c.getConsoleProject() == project && c.isShowing());
    }

    /**
     * listener is called when a console is added, i.e. {@link FregeReplViewMap#addConsole} is called
     *
     * @param listener listener to be called
     */
    public static void addConsoleAdditionListener(Consumer<FregeReplView> listener) {
        consoleAdditionListeners.add(listener);
    }

    public static void removeConsoleAdditionListener(Consumer<FregeReplView> listener) {
        consoleAdditionListeners.remove(listener);
    }

    /**
     * listener is called when a console is removed, i.e. {@link FregeReplViewMap#removeConsole} is called
     *
     * @param listener listener to be called
     */
    public static void addConsoleRemovalListener(Consumer<FregeReplView> listener) {
        consoleRemovalListeners.add(listener);
    }

    public static void removeConsoleRemovalListener(Consumer<FregeReplView> listener) {
        consoleRemovalListeners.remove(listener);
    }
}
