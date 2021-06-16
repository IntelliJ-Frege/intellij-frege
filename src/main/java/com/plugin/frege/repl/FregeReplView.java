package com.plugin.frege.repl;

import com.intellij.execution.console.ConsoleHistoryController;
import com.intellij.execution.console.ConsoleRootType;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.ex.DocumentEx;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.DocumentUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FregeReplView extends LanguageConsoleImpl {
    private final @NotNull Project consoleProject;

    private final ConsoleRootType consoleRootType = new ConsoleRootType("frege", "Frege") {
    };
    private final List<String> modulesToLoad;

    private ConsoleHistoryController historyController;
    private OutputStreamWriter outputStreamWriter;

    public FregeReplView(@NotNull Project project, @NotNull String title, @NotNull Language language, List<String> modulesToLoad) {
        super(project, title, language);

        this.consoleProject = project;
        this.modulesToLoad = modulesToLoad;
        setPrompt("frege> ");
    }

    @NotNull
    public Project getConsoleProject() {
        return consoleProject;
    }


    // TODO add MessageFilter (for urls)

    @Override
    public void attachToProcess(@NotNull ProcessHandler processHandler) {
        super.attachToProcess(processHandler);
        OutputStream processInput = processHandler.getProcessInput();
        if (processInput != null) {
            outputStreamWriter = new OutputStreamWriter(processInput);
            historyController = new ConsoleHistoryController(consoleRootType, "frege", this);
            historyController.install();
            FregeReplViewMap.addConsole(this);

            modulesToLoad.stream().map(m -> ModuleManager.getInstance(consoleProject).findModuleByName(m)).
                    forEach(this::loadAllFregeFilesInModule);
        }
    }

    // get source roots of module and of its dependencies
    private void getAllSourceRoots(Module module, List<VirtualFile> roots, Set<Module> visited) {
        if (visited.contains(module)) return;
        visited.add(module);
        roots.addAll(Arrays.asList(ModuleRootManager.getInstance(module).getSourceRoots()));
        Arrays.stream(ModuleRootManager.getInstance(module).getDependencies()).forEach(m -> getAllSourceRoots(m, roots, visited));
    }

    public void loadAllFregeFilesInModule(Module module) {
        List<VirtualFile> contentRoots = Arrays.asList(ModuleRootManager.getInstance(module).getContentRoots());
        List<VirtualFile> sourceRoots = new ArrayList<>();
        getAllSourceRoots(module, sourceRoots, new HashSet<>());
        List<VirtualFile> rootsToWalk = sourceRoots.isEmpty() ? contentRoots : sourceRoots;
        rootsToWalk.stream().flatMap(vf -> {
            try {
                return Files.walk(vf.toNioPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".fr")).forEach(path -> {
            try {
                loadFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            outputStreamWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFile(Path path) throws IOException {
        if (!path.toString().endsWith(".fr")) {
            throw new IllegalArgumentException("Unable to load file with extension different to .fr");
        }

        String loadFileString = String.format(":load %s\n", path);
        historyController.addToHistory(loadFileString);
        outputStreamWriter.write(loadFileString);
    }

    @Override
    public void dispose() {
        super.dispose();
        try {
            outputStreamWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FregeReplViewMap.delConsole(this);
    }

    public void execute() {
        EditorEx consoleEditor = getConsoleEditor();
        DocumentEx editorDocument = consoleEditor.getDocument();
        String text = editorDocument.getText();

        DocumentUtil.writeInRunUndoTransparentAction(() -> consoleEditor.getDocument().deleteString(0, text.length()));

        executeCommand(text);
    }

    public void executeCommand(String text) {
        String trimmedCommand = text.trim();
        historyController.addToHistory(trimmedCommand);

        String commandInputText = trimmedCommand + "\n";

        try {
            outputStreamWriter.write(commandInputText);
            outputStreamWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        scrollToEnd();
    }
}
