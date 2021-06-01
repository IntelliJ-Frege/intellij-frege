package com.plugin.frege.lexer.layout;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import com.plugin.frege.lexer.FregeLexerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FregeLayoutLexerAdapter extends LexerBase {
    private final @NotNull FregeLexerAdapter lexer = new FregeLexerAdapter();
    private @Nullable FregeLayoutLexer layoutLexer;

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        if (startOffset != 0) {
            throw new FregeLayoutLexerException(
                    new UnsupportedOperationException("Does not support incremental lexing: startOffset must be 0"));
        }
        if (initialState != 0) {
            throw new FregeLayoutLexerException(
                    new UnsupportedOperationException("Does not support incremental lexing: initialState must be 0"));
        }
        lexer.start(buffer, startOffset, endOffset, initialState);
        layoutLexer = new FregeLayoutLexer(lexer);
    }

    @Override
    public int getState() {
        return lexer.getState();
    }

    @Override
    public @Nullable IElementType getTokenType() {
        if (layoutLexer == null) {
            throw new FregeLayoutLexerException(
                    new IllegalStateException("Call getTokenType before the start"));
        }
        return layoutLexer.getCurrentToken().type;
    }

    @Override
    public int getTokenStart() {
        if (layoutLexer == null) {
            throw new FregeLayoutLexerException(
                    new IllegalStateException("Call getTokenStart before the start"));
        }
        return layoutLexer.getCurrentToken().start;
    }

    @Override
    public int getTokenEnd() {
        if (layoutLexer == null) {
            throw new FregeLayoutLexerException(
                    new IllegalStateException("Call getTokenEnd before the start"));
        }
        return layoutLexer.getCurrentToken().end;
    }

    @Override
    public @NotNull String getTokenText() {
        if (layoutLexer == null) {
            throw new FregeLayoutLexerException(
                    new IllegalStateException("Call getTokenText before the start"));
        }
        return layoutLexer.getCurrentToken().text;
    }

    @Override
    public void advance() {
        if (layoutLexer == null) {
            throw new FregeLayoutLexerException(
                    new IllegalStateException("Call advance before the start"));
        }
        layoutLexer.advance();
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
