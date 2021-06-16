package com.plugin.frege.repl;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.plugin.frege.FregeFileType;
import com.plugin.frege.gradle.GradleFregeException;
import com.plugin.frege.gradle.GradleFregePropertiesUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FregeReplState extends CommandLineState {
    private final FregeReplRunConfiguration configuration;

    public FregeReplState(FregeReplRunConfiguration configuration, ExecutionEnvironment environment) {
        super(environment);
        this.configuration = configuration;

        String moduleName = configuration.getSelectedModuleName();
        if (moduleName == null) {
            throw new RuntimeException("Module is not selected in Frege REPL run configuration");
        }

        setConsoleBuilder(new TextConsoleBuilder() {
            @Override
            public @NotNull ConsoleView getConsole() {
                return new FregeReplView(configuration.getProject(),
                        "Frege REPL",
                        FregeFileType.INSTANCE.getLanguage(),
                        configuration.getModulesToLoad());
            }

            @Override
            public void addFilter(@NotNull Filter filter) {
            }

            @Override
            public void setViewer(boolean isViewer) {
            }
        });
    }

    // TODO exit if no repl in compiler
    @Override
    protected @NotNull ProcessHandler startProcess() throws ExecutionException {
        if (configuration.getSelectedModuleName() == null) throw new ExecutionException("No module selected");
        Module module = ModuleManager.getInstance(configuration.getProject()).findModuleByName(configuration.getSelectedModuleName());
        String compilerJar;
        try {
            compilerJar = GradleFregePropertiesUtils.getCompilerJar(module);
        } catch (GradleFregeException e) {
            throw new ExecutionException(e);
        }

        Sdk projectSdk = ProjectRootManager.getInstance(configuration.getProject()).getProjectSdk();
        String pathToJavaExecutable = null;
        if (projectSdk != null) {
            String sdkHomePath = projectSdk.getHomePath();
            if (sdkHomePath != null) {
                Path possiblePathToJavaExecutable = Path.of(sdkHomePath).resolve("bin/java");
                if (possiblePathToJavaExecutable.toFile().exists())
                    pathToJavaExecutable = possiblePathToJavaExecutable.toString();
            }
        }
        if (pathToJavaExecutable == null) {
            pathToJavaExecutable = "java"; // system default
        }

        GeneralCommandLine commandLine = new GeneralCommandLine().
                withExePath(pathToJavaExecutable).
                withWorkDirectory(configuration.getProject().getBasePath()).
                withParameters("-jar", compilerJar);
        if (!configuration.getAdditionalArguments().equals(""))
            commandLine.addParameters(configuration.getAdditionalArguments().split(" "));
        FregeReplProcessHandler handler = new FregeReplProcessHandler(commandLine.createProcess(), commandLine.getCommandLineString(), StandardCharsets.UTF_8, 1);

        ProcessTerminatedListener.attach(handler);
        return handler;
    }
}
