package com.plugin.frege.runConfiguration;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;

// TODO docs, link to BaseInputStreamReader
// TODO ScalaLanguageConsole.scala, 224 line, same hack, but much more easy
public class FregeReplReader extends Reader {
    private final Reader in;
    private final int skipNLastLines;
    private final Deque<Character> buffer = new ArrayDeque<>();
    private int breaksCount = 0;

    public FregeReplReader(Reader in, int skipNLastLines) {
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
        int bytesReadFromIn = in.read(readFromIn, 0, len);
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
        return in.ready();
    }
}
