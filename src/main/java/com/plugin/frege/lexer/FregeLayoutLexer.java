package com.plugin.frege.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.Stack;

import static com.plugin.frege.psi.FregeTypes.*;

public class FregeLayoutLexer {
    private static final TokenSet SECTION_CREATING_KEYWORDS = TokenSet.create(WHERE, LET, OF, DO);
    private static final TokenSet NON_CODE_TOKENS = TokenSet.create(TokenType.WHITE_SPACE, NEW_LINE, LINE_COMMENT, BLOCK_COMMENT);

    private final @NotNull FregeLexerAdapter lexer;
    private final @NotNull Deque<@NotNull Token> processedTokens = new ArrayDeque<>();
    private final @NotNull Queue<@NotNull Token> virtualPrefix = new ArrayDeque<>();
    private final @NotNull Stack<@NotNull Integer> indentStack = new Stack<>();
    private @Nullable Token newlineStickyToken = null;
    private @NotNull State state = State.START;
    private @NotNull Token.Line currentLine = new Token.Line();
    private int currentColumn = 0;
    private int codeTokensScanned = 0;
    private @Nullable IElementType firstCodeTokenType;


    public FregeLayoutLexer(@NotNull FregeLexerAdapter lexer) {
        this.lexer = lexer;
        indentStack.push(-1); // top-level section
    }

    private static @NotNull Token createVirtualToken(IElementType elementType, Token precedesToken) {
        return new Token(elementType, precedesToken.start,
                precedesToken.end, precedesToken.column, "", precedesToken.line);
    }

    public @NotNull Token getCurrentToken() {
        if (virtualPrefix.isEmpty() && processedTokens.isEmpty()) {
            processMoreTokens();
        }
        if (!virtualPrefix.isEmpty()) {
            return virtualPrefix.element();
        } else {
            return processedTokens.getFirst();
        }
    }

    public void advance() {
        if (!getCurrentToken().isEof()) {
            if (!virtualPrefix.isEmpty()) {
                virtualPrefix.remove();
            } else {
                processedTokens.removeFirst();
            }
        }
    }

    private @NotNull Token findPrecedesForCurrentToken() {
        if (processedTokens.isEmpty()) {
            if (newlineStickyToken == null) {
                throw new FregeLayoutLexerException("Cannot find precedes token");
            }
            return newlineStickyToken;
        } else {
            return processedTokens.getLast();
        }
    }

    private boolean isModuleKeyword(@Nullable IElementType type) {
        return MODULE.equals(type) || PACKAGE.equals(type);
    }

    /**
     * We can't lex Frege incrementally because we have to create virtual tokens to follow the layout rule,
     * but we can lex it lazily, giving tokens part by part. This function gets the next part of tokens from lexer and processes them
     * adding new tokens to {@link FregeLayoutLexer#processedTokens} and {@link FregeLayoutLexer#virtualPrefix}.
     * If it's possible we want to create virtual tokens right after the newline tokens, but it is likely that
     * there are a lot of consecutive newline tokens, so we have to create virtual tokens right after the first one.
     *
     * {@link FregeLayoutLexer#virtualPrefix} contains the tokens that we need first. They end the last
     * processed part. Then we use {@link FregeLayoutLexer#processedTokens} which consists of initial tokens and virtual open section tokens.
     * The only exception is one-line let-in expression, then we have to create a virtual end section token in the same line.
     *
     * @see <a href="https://www.haskell.org/onlinereport/haskell2010/haskellch2.html#x7-210002.7">Haskell layout rule</a>
     */
    private void processMoreTokens() {
        boolean wasCodeTokenInBlock = false;
        boolean wasLetTokenInBlock = false;
        while (true) {
            Token token = getTokenFromLexer();
            if (token.elementType == NEW_LINE && wasCodeTokenInBlock) {
                processedTokens.addLast(token);
                newlineStickyToken = token;
                break;
            }
            if (token.isEof()) {
                if (indentStack.size() > 2) {
                    Token precedes = findPrecedesForCurrentToken();
                    for (int i = 2; i < indentStack.size(); i++) {
                        processedTokens.addLast(createVirtualToken(VIRTUAL_END_SECTION, precedes));
                    }
                }
                processedTokens.addLast(token);
                break;
            }
            if (!token.isCode()) {
                processedTokens.addLast(token);
                continue;
            }
            codeTokensScanned++;
            if (codeTokensScanned == 2) {
                if (!isModuleKeyword(firstCodeTokenType) && (!PROTECTED_MODIFIER.equals(firstCodeTokenType) || !isModuleKeyword(token.elementType))) {
                    indentStack.push(0);
                }
            }
            if (LET.equals(token.elementType)) {
                wasLetTokenInBlock = true;
            }
            switch (state) {
                case START:
                    if (token.column == 0) {
                        state = State.NORMAL;
                        firstCodeTokenType = token.elementType;
                    }
                    break;
                case WAITING_FOR_SECTION_START:
                    if (LEFT_BRACE.equals(token.elementType)) {
                        state = State.NORMAL;
                    } else if (token.isCode() && token.column > indentStack.peek()) {
                        processedTokens.addLast(createVirtualToken(VIRTUAL_OPEN_SECTION, findPrecedesForCurrentToken()));
                        state = State.NORMAL;
                        indentStack.push(token.column);
                    } else if (token.isFirstSignificantTokenOnLine() && token.column <= indentStack.peek()) { // TODO
                        state = State.NORMAL;
                    }
                    break;
                case NORMAL:
                    if (token.isFirstSignificantTokenOnLine()) {
                        if (newlineStickyToken == null) {
                            throw new FregeLayoutLexerException("Cannot find sticky newline token");
                        }
                        while (token.column <= indentStack.peek()) {
                            if (token.column == indentStack.peek()) {
                                virtualPrefix.add(createVirtualToken(VIRTUAL_END_DECL, newlineStickyToken));
                                break;
                            } else if (token.column < indentStack.peek()) {
                                virtualPrefix.add(createVirtualToken(VIRTUAL_END_SECTION, newlineStickyToken));
                                indentStack.pop();
                            }
                        }
                    } else if (IN.equals(token.elementType) && wasLetTokenInBlock) {
                        processedTokens.addLast(createVirtualToken(VIRTUAL_END_SECTION, findPrecedesForCurrentToken()));
                        indentStack.pop();
                    }
                    break;
            }
            processedTokens.addLast(token);
            if (SECTION_CREATING_KEYWORDS.contains(token.elementType)) {
                state = State.WAITING_FOR_SECTION_START;
            }
            wasCodeTokenInBlock = true;
        }
    }

    private @NotNull Token getTokenFromLexer() {
        Token token = new Token(lexer.getTokenType(),
                lexer.getTokenStart(),
                lexer.getTokenEnd(),
                currentColumn,
                lexer.getTokenText(),
                currentLine);
        if (currentLine.columnWhereCodeStarts == null && token.isCode()) {
            currentLine.columnWhereCodeStarts = currentColumn;
        }
        currentColumn += token.end - token.start;
        if (token.elementType == NEW_LINE) {
            currentLine = new Token.Line();
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

    public static class Token {
        public final @Nullable IElementType elementType;
        public final int start;
        public final int end;
        public final int column;
        public final @NotNull String tokenText;
        public final @NotNull Line line;

        public Token(@Nullable IElementType elementType, int start, int end, int column, @NotNull String tokenText, @NotNull Line line) {
            this.elementType = elementType;
            this.start = start;
            this.end = end;
            this.column = column;
            this.tokenText = tokenText;
            this.line = line;
        }

        @Override
        public String toString() {
            return elementType == null ? "EOF" : elementType.toString() + "(" + start + ", " + end + ")";
        }

        public boolean isEof() {
            return elementType == null;
        }

        public boolean isCode() {
            return !NON_CODE_TOKENS.contains(elementType) && !isEof();
        }

        public boolean isFirstSignificantTokenOnLine() {
            return isCode() &&
                    line.columnWhereCodeStarts != null &&
                    line.columnWhereCodeStarts == column;
        }

        public static class Line {
            public @Nullable Integer columnWhereCodeStarts;
        }
    }
}
