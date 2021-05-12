package com.plugin.frege.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FregeLayoutLexerAdapter extends LexerBase {
    private final @NotNull FregeLexerAdapter lexer = new FregeLexerAdapter();
    private @Nullable FregeLayoutLexer fregeLayoutLexer;

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        if (startOffset != 0) {
            throw new RuntimeException("Does not support incremental lexing: startOffset must be 0");
        }
        if (initialState != 0) {
            throw new RuntimeException("Does not support incremental lexing: initialState must be 0");
        }
        lexer.start(buffer, startOffset, endOffset, initialState);
        fregeLayoutLexer = new FregeLayoutLexer(lexer);
    }

    @Override
    public int getState() {
        return lexer.getState();
    }

    @Override
    public @Nullable IElementType getTokenType() {
        if (fregeLayoutLexer == null) {
            throw new FregeLayoutLexerException("Call getTokenType before the start");
        }
        return fregeLayoutLexer.getCurrentToken().elementType;
    }

    @Override
    public int getTokenStart() {
        if (fregeLayoutLexer == null) {
            throw new FregeLayoutLexerException("Call getTokenStart before the start");
        }
        return fregeLayoutLexer.getCurrentToken().start;
    }

    @Override
    public int getTokenEnd() {
        if (fregeLayoutLexer == null) {
            throw new FregeLayoutLexerException("Call getTokenEnd before the start");
        }
        return fregeLayoutLexer.getCurrentToken().end;
    }

    @Override
    public @NotNull String getTokenText() {
        if (fregeLayoutLexer == null) {
            throw new FregeLayoutLexerException("Call getTokenText before the start");
        }
        return fregeLayoutLexer.getCurrentToken().tokenText;
    }

    @Override
    public void advance() {
        if (fregeLayoutLexer == null) {
            throw new FregeLayoutLexerException("Call advance before the start");
        }
        fregeLayoutLexer.advance();
    }

    @Override
    public @NotNull CharSequence getBufferSequence() {
        return lexer.getBufferSequence();
    }

    @Override
    public int getBufferEnd() {
        return 0;
    }

}
