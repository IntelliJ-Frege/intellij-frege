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
    private static final TokenSet SECTION_CREATING_KEYWORDS;
    private static final TokenSet NON_CODE_TOKENS;

    static {
        SECTION_CREATING_KEYWORDS = TokenSet.create(WHERE, LET, OF, DO);
        NON_CODE_TOKENS = TokenSet.create(TokenType.WHITE_SPACE, NEW_LINE, LINE_COMMENT, BLOCK_COMMENT);
    }

    private final FregeLexerAdapter lexer = new FregeLexerAdapter();
    private int currentTokenIndex = 0;
    private ArrayList<Token> tokens = new ArrayList<>();

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

        getTokens();
        layoutTokens();
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

    private Token createVirtualToken(IElementType elementType, Token precedesToken) {
        return new Token(elementType, precedesToken.start,
                precedesToken.end, precedesToken.column, precedesToken.line);
    }

    private void getTokens() {
        tokens = new ArrayList<>();
        Token.Line line = new Token.Line();
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
                line = new Token.Line();
                currentColumn = 0;
            }
            lexer.advance();
        }
    }

    private void layoutTokens() {
        State state = State.START;
        Stack<Integer> indentStack = new Stack<>();
        indentStack.push(-1); // top-level section
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            switch (state) {
                case START:
                    if (token.isCode() && token.column == 0) {
                        state = State.NORMAL;
                    }
                    break;
                case WAITING_FOR_SECTION_START:
                    if (token.isCode() && token.elementType.equals(LEFT_BRACE)) {
                        state = State.NORMAL;
                    }
                    else if (token.isCode() && token.column > indentStack.peek()) {
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
                    }
                    if (token.isFirstSignificantTokenOnLine()) {
                        int insertAt = getInsertionPos(i);
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
                    } else if (isSingleLineLetIn(i)) {
                        tokens.add(i, createVirtualToken(VIRTUAL_END_SECTION, tokens.get(i - 1)));
                        i++;
                        indentStack.pop();
                    }
                    break;
            }
        }
        if (indentStack.size() > 2) {
            int insertAt = getInsertionPos(tokens.size() - 1);
            Token precedingToken = tokens.get(insertAt - 1);
            for (int j = 0; j < indentStack.size() - 2; j++) {
                tokens.add(insertAt, createVirtualToken(VIRTUAL_END_SECTION, precedingToken));
                insertAt++;
            }
        }
    }

    private int getInsertionPos(int i) {
        if (i == tokens.size() - 1) {
            return i;
        }
        int insertAt = i;
        boolean findInsertPos = false;
        for (int k = i - 1; k > 0; k--) {
            if (tokens.get(k).isCode()) {
                for (int m = k + 1; m <= i; m++) {
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
        return insertAt;
    }

    private boolean isSingleLineLetIn(int index) { // TODO
        Token token = tokens.get(index);
        if (token == null || token.elementType == null || !token.elementType.equals(IN)) {
            return false;
        }
        for (int i = index - 1; i >= 0; i--) {
            Token currentToken = tokens.get(i);
            if (!currentToken.line.equals(token.line)) {
                break;
            }
            if (currentToken.elementType.equals(LET)) {
                return true;
            }
        }
        return false;
    }

    private enum State {
        START,
        WAITING_FOR_SECTION_START,
        NORMAL
    }

    private static class Token {
        private final IElementType elementType;
        private final int start;
        private final int end;
        private final int column;
        private final Line line;

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

        private static class Line {
            private Integer columnWhereCodeStarts;
        }
    }
}
