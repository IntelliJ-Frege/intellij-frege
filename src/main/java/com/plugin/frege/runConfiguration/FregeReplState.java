package com.plugin.frege.runConfiguration;

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
import com.plugin.frege.FregeFileType;
import com.plugin.frege.gradle.GradleFregeException;
import com.plugin.frege.gradle.GradleFregePropertiesUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class FregeReplState extends CommandLineState {
    private final FregeRunConfiguration configuration;

    public FregeReplState(FregeRunConfiguration configuration, ExecutionEnvironment environment) {
        super(environment);
        this.configuration = configuration;

        String moduleName = configuration.getSelectedModuleName();
        if (moduleName == null) {
            throw new RuntimeException("Module is not selected in Frege REPL run configuration");
        }
        Module module = ModuleManager.getInstance(configuration.getProject()).findModuleByName(moduleName);

        setConsoleBuilder(new TextConsoleBuilder() {
            @Override
            public @NotNull ConsoleView getConsole() {
                return new FregeConsoleView(configuration.getProject(),
                        "Frege REPL",
                        FregeFileType.INSTANCE.getLanguage(),
                        module,
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

        GeneralCommandLine commandLine = new GeneralCommandLine().
                withExePath("/usr/bin/java").
                withWorkDirectory(configuration.getProject().getBasePath()).
                withParameters("-jar", compilerJar);
        if (!configuration.getAdditionalArguments().equals(""))
            commandLine.addParameters(configuration.getAdditionalArguments().split(" "));
        FregeConsoleProcessHandler handler = new FregeConsoleProcessHandler(commandLine.createProcess(), commandLine.getCommandLineString(), StandardCharsets.UTF_8, 1);
        // TODO If it's a long-running mostly idle daemon process, consider overriding OSProcessHandler#readerOptions with 'BaseOutputReader.Options.forMostlySilentProcess()' to reduce CPU usage.
        ProcessTerminatedListener.attach(handler);
        return handler;
    }
}
