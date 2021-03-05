package com.plugin.frege.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Stack;

import static com.plugin.frege.psi.FregeTypes.*;

public class FregeLayoutLexer extends LexerBase {
    private final FregeLexerAdapter lexer = new FregeLexerAdapter();
    private int currentTokenIndex = 0;
    private ArrayList<Token> tokens = new ArrayList<>();
    private static final TokenSet SECTION_CREATING_KEYWORDS;
    private static final TokenSet NON_CODE_TOKENS;

    static {
        SECTION_CREATING_KEYWORDS = TokenSet.create(WHERE, LET, OF, DO);
        NON_CODE_TOKENS = TokenSet.create(TokenType.WHITE_SPACE, NEW_LINE, LINE_COMMENT, BLOCK_COMMENT);
    }

    private Token getCurrentToken() {
        return tokens.get(currentTokenIndex);
    }

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        if (startOffset != 0) {
            throw new RuntimeException("Does not support incremental lexing: startOffset must be 0");
        }
        if (initialState != 0) {
            throw new RuntimeException("Does not support incremental lexing: initialState must be 0");
        }
        lexer.start(buffer, startOffset, endOffset, initialState);

        tokens = doLayout();
        currentTokenIndex = 0;
    }

    @Override
    public int getState() {
        return lexer.getState();
    }

    @Override
    public @Nullable IElementType getTokenType() {
        return getCurrentToken().elementType;
    }

    @Override
    public int getTokenStart() {
        return getCurrentToken().start;
    }

    @Override
    public int getTokenEnd() {
        return getCurrentToken().end;
    }

    @Override
    public void advance() {
        if (!getCurrentToken().isEof()) {
            currentTokenIndex++;
        }
    }

    @Override
    public @NotNull CharSequence getBufferSequence() {
        return lexer.getBufferSequence();
    }

    @Override
    public int getBufferEnd() {
        return 0;
    }

    private enum State {
        START,
        WAITING_FOR_SECTION_START,
        NORMAL
    }

    private Token createVirtualToken(IElementType elementType, Token precedesToken) {
        return new Token(elementType, precedesToken.start,
                precedesToken.end, precedesToken.column, precedesToken.line);
    }

    private ArrayList<Token> slurpTokens() {
        ArrayList<Token> tokens = new ArrayList<>();
        Line line = new Line();
        int currentColumn = 0;
        while (true) {
            Token token = new Token(lexer.getTokenType(),
                    lexer.getTokenStart(),
                    lexer.getTokenEnd(),
                    currentColumn,
                    line);
            tokens.add(token);
            if (line.columnWhereCodeStarts == null && token.isCode()) {
                line.columnWhereCodeStarts = currentColumn;
            }
            currentColumn += token.end - token.start;
            if (token.isEof()) {
                break;
            } else if (token.elementType == NEW_LINE) {
                line = new Line();
                currentColumn = 0;
            }
            lexer.advance();
        }
        return tokens;
    }

    private ArrayList<Token> doLayout() {
        ArrayList<Token> tokens = slurpTokens();
        int i = 0;
        State state = State.START;
        Stack<Integer> indentStack = new Stack<>();
        indentStack.push(-1); // top-level section
        do {
            Token token = tokens.get(i);
            switch (state) {
                case START:
                    if (token.isCode() && token.column == 0) {
                        state = State.NORMAL;
                    }
                    break;
                case WAITING_FOR_SECTION_START:
                    if (token.isCode() && token.column > indentStack.peek()) {
                        tokens.add(i, createVirtualToken(VIRTUAL_OPEN_SECTION, tokens.get(i - 1)));
                        i++;
                        state = State.NORMAL;
                        indentStack.push(token.column);
                    } else if (token.isFirstSignificantTokenOnLine() && token.column <= indentStack.peek()) {
                        state = State.NORMAL;
                        i--;
                    }
                    break;
                case NORMAL:
                    if (SECTION_CREATING_KEYWORDS.contains(token.elementType)) {
                        state = State.WAITING_FOR_SECTION_START;
                    } else if (token.isFirstSignificantTokenOnLine()) {
                        int insertAt = i;
                        boolean findInsertPos = false;
                        for (int k = i - 1; k >= 1; k--) {
                            if (tokens.get(k).isCode()) {
                                for (int m = k + 1; m < i + 1; m++) {
                                    if (tokens.get(m).elementType == NEW_LINE) {
                                        insertAt = m + 1;
                                        findInsertPos = true;
                                        break;
                                    }
                                }
                                if (findInsertPos) {
                                    break;
                                }
                            }
                        }

                        Token precedingToken = tokens.get(insertAt - 1);
                        while (token.column <= indentStack.peek()) {
                            if (token.column == indentStack.peek()) {
                                tokens.add(insertAt, createVirtualToken(VIRTUAL_END_DECL, precedingToken));
                                i++;
                                break;
                            } else if (token.column < indentStack.peek()) {
                                tokens.add(insertAt, createVirtualToken(VIRTUAL_END_SECTION, precedingToken));
                                i++;
                                insertAt++;
                                indentStack.pop();
                            }
                        }
                    }
                    break;
            }
            i++;
        } while (i < tokens.size());
        for (int j = 0; j < indentStack.size() - 1; j++) {
            tokens.add(tokens.size() - 1, createVirtualToken(VIRTUAL_END_SECTION, tokens.get(tokens.size() - 1)));
        }
        return tokens;
    }

    private static class Line {
        private Integer columnWhereCodeStarts;
    }

    private static class Token {
        IElementType elementType;
        int start;
        int end;
        int column;
        Line line;

        public Token(IElementType elementType, int start, int end, int column, Line line) {
            this.elementType = elementType;
            this.start = start;
            this.end = end;
            this.column = column;
            this.line = line;
        }

        @Override
        public String toString() {
            return elementType.toString() + "(" + start + ", " + end + ")";
        }

        boolean isEof() {
            return elementType == null;
        }

        boolean isCode() {
            return !NON_CODE_TOKENS.contains(elementType) && !isEof();
        }

        boolean isFirstSignificantTokenOnLine() {
            return isCode() && column == line.columnWhereCodeStarts;
        }
    }
}
