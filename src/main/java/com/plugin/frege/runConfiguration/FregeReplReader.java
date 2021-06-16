package com.plugin.frege.runConfiguration;

import com.intellij.util.io.BaseInputStreamReader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * HACK:
 * We want
 */
// TODO docs, link to BaseInputStreamReader
// TODO ScalaLanguageConsole.scala, 224 line, same hack, but much more easy
public class FregeReplReader extends BaseInputStreamReader {
    private final InputStream in;
    private final int skipNLastLines;
    private final Deque<Character> buffer = new ArrayDeque<>();
    private int breaksCount = 0;

    public FregeReplReader(InputStream in, Charset charset, int skipNLastLines) {
        super(in, charset);
        this.in = in;
        this.skipNLastLines = skipNLastLines;
    }

    private boolean isLineBreak(Character b) {
        return '\n' == b;
    }

    @Override
    public int read(char @NotNull [] cbuf, int off, int len) throws IOException {
        System.err.println("One read");
        char[] readFromIn = new char[len];
        int bytesReadFromIn = super.read(readFromIn, 0, len);
        new String(readFromIn, 0, bytesReadFromIn).chars().forEach(c -> {
            if (isLineBreak((char) c)) {
                breaksCount++;
            }
            buffer.add((char) c);
            System.err.print((char) c);
        });
        System.err.println("");
        int bytesRead;
        for (bytesRead = 0; bytesRead < len; bytesRead++) {
            if (buffer.isEmpty()) break;
            Character c = buffer.pollFirst();
            if (breaksCount > skipNLastLines || (breaksCount == skipNLastLines && (!isLineBreak(c)))) {
                cbuf[off + bytesRead] = c;
                if (isLineBreak(c)) {
                    --breaksCount;
                }
            } else {
                buffer.addFirst(c);
                break;
            }
        }
        return bytesRead;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public boolean ready() throws IOException {
        return super.ready();
    }
}
