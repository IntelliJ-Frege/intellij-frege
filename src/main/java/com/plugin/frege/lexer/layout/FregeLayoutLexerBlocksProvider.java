package com.plugin.frege.lexer.layout;

import com.intellij.psi.tree.TokenSet;
import com.plugin.frege.lexer.FregeLexerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.plugin.frege.psi.FregeTypes.*;

public class FregeLayoutLexerBlocksProvider implements Iterator<FregeLayoutLexerBlock> {
    private static final TokenSet SECTION_CREATING_KEYWORDS = TokenSet.create(WHERE, LET, OF, DO);
    private final @NotNull FregeLexerAdapter lexer;
    private final @NotNull FregeLayoutLexerBlocksBuilder builder = new FregeLayoutLexerBlocksBuilder();
    private @NotNull State state = State.START;
    private @NotNull FregeLayoutLexerToken.Line currentLine = new FregeLayoutLexerToken.Line();
    private boolean isEof = false;
    private int currentColumn = 0;
    private int codeTokensScanned = 0;
    private @Nullable FregeLayoutLexerToken firstCodeToken;

    public FregeLayoutLexerBlocksProvider(@NotNull FregeLexerAdapter lexer) {
        this.lexer = lexer;
    }

    @Override
    public boolean hasNext() {
        return !isEof;
    }

    @Override
    public @NotNull FregeLayoutLexerBlock next() {
        if (!hasNext()) {
            throw new FregeLayoutLexerException(
                    new NoSuchElementException());
        }
        while (true) {
            FregeLayoutLexerToken token = getTokenFromLexer();
            if (token.isEof()) {
                isEof = true;
            }
            if (builder.canFinishBlockWith(token)) {
                return builder.finishBlock(token);
            }
            if (!token.isCode()) {
                builder.add(token);
                continue;
            }
            codeTokensScanned++;
            if (codeTokensScanned == 2) {
                if (firstCodeToken == null) {
                    throw new FregeLayoutLexerException(
                            new IllegalStateException("First code token is undefined"));
                }
                if (firstCodeToken.isNotModuleStart() && (!firstCodeToken.isProtectModifier() || token.isNotModuleStart())) {
                    builder.insertFakeModuleSection();
                }
            }
            switch (state) {
                case START:
                    if (token.column == 0) {
                        state = State.NORMAL;
                        firstCodeToken = token;
                    }
                    break;
                case WAITING_FOR_SECTION_START:
                    builder.tryStartSectionWith(token);
                    state = State.NORMAL;
                    break;
                case NORMAL:
                    if (!builder.tryHandleSingleLineLetIn(token)) {
                        builder.tryHandleSectionEndOrDeclEnd(token);
                    }
                    break;
            }
            if (SECTION_CREATING_KEYWORDS.contains(token.type)) {
                state = State.WAITING_FOR_SECTION_START;
            }
            builder.add(token);
        }
    }

    private @NotNull FregeLayoutLexerToken getTokenFromLexer() {
        FregeLayoutLexerToken token = new FregeLayoutLexerToken(lexer.getTokenType(),
                lexer.getTokenStart(),
                lexer.getTokenEnd(),
                currentColumn,
                lexer.getTokenText(),
                currentLine);
        if (currentLine.columnWhereCodeStarts == null && token.isCode()) {
            currentLine.columnWhereCodeStarts = currentColumn;
        }
        currentColumn += token.end - token.start;
        if (token.isNewLine()) {
            currentLine = new FregeLayoutLexerToken.Line();
            currentColumn = 0;
        }
        lexer.advance();
        return token;
    }

    private enum State {
        START,
        WAITING_FOR_SECTION_START,
        NORMAL
    }

}
