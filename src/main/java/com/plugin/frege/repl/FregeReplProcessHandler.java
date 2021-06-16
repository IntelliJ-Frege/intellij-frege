package com.plugin.frege.repl;

import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.util.io.BaseOutputReader;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;

public class FregeReplProcessHandler extends ColoredProcessHandler {
    private final int skipNLastLines;
    private @NotNull
    final Process process;
    private final @NotNull Charset charset;

    public FregeReplProcessHandler(@NotNull Process process, String commandLine, @NotNull Charset charset, int skipNLastLines) {
        super(process, commandLine, charset);
        this.process = process;
        this.charset = charset;
        this.skipNLastLines = skipNLastLines;
    }

    @Override
    protected @NotNull Reader createProcessOutReader() {
        return new FregeReplReader(process.getInputStream(), charset, skipNLastLines);
    }

    @NotNull
    @Override
    protected BaseOutputReader.Options readerOptions() {
        return BaseOutputReader.Options.forMostlySilentProcess();
    }
}
