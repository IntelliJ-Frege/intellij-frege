package com.plugin.frege.runConfiguration;

import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.util.io.BaseInputStreamReader;
import com.intellij.util.io.BaseOutputReader;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;

public class FregeConsoleProcessHandler extends ColoredProcessHandler {
    private final int skipNLastLines;
    private @NotNull
    final Process process;
    private final @NotNull Charset charset;

    public FregeConsoleProcessHandler(@NotNull Process process, String commandLine, @NotNull Charset charset, int skipNLastLines) {
        super(process, commandLine, charset);
        this.process = process;
        this.charset = charset;
        this.skipNLastLines = skipNLastLines;
    }

    @Override
    protected @NotNull Reader createProcessOutReader() {
        System.err.println("Out reader created");
        return new FregeReplReader(process.getInputStream(), charset, skipNLastLines);
    }

    @NotNull
    @Override
    protected BaseOutputReader.Options readerOptions() {
        return BaseOutputReader.Options.forMostlySilentProcess();
    }
}
