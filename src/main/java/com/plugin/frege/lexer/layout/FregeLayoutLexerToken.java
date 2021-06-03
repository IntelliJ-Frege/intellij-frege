package com.plugin.frege.lexer.layout;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.plugin.frege.psi.FregeTypes.*;

public class FregeLayoutLexerToken {
    private static final @NotNull TokenSet NON_CODE_TOKENS = TokenSet.create(TokenType.WHITE_SPACE, NEW_LINE, LINE_COMMENT, BLOCK_COMMENT);
    private static final @NotNull TokenSet VIRTUAL_TOKENS = TokenSet.create(VIRTUAL_OPEN_SECTION, VIRTUAL_END_DECL, VIRTUAL_END_SECTION);
    public final @Nullable IElementType type;
    public final int start;
    public final int end;
    public final int column;
    public final @NotNull String text;
    public final @NotNull Line line;
    public boolean isSectionGenerating = false;

    public FregeLayoutLexerToken(@Nullable IElementType type, int start, int end, int column, @NotNull String text, @NotNull Line line) {
        this.type = type;
        this.start = start;
        this.end = end;
        this.column = column;
        this.text = text;
        this.line = line;
    }

    public static @NotNull FregeLayoutLexerToken createVirtualToken(@NotNull IElementType type,
                                                                    @NotNull FregeLayoutLexerToken precedesToken) {
        if (!VIRTUAL_TOKENS.contains(type)) {
            throw new IllegalArgumentException(type + " is not a virtual type");
        }
        FregeLayoutLexerToken token = new FregeLayoutLexerToken(type, precedesToken.start,
                precedesToken.end, precedesToken.column, "", precedesToken.line);
        if (VIRTUAL_OPEN_SECTION.equals(type)) {
            token.isSectionGenerating = true;
        }
        return token;
    }

    @Override
    public String toString() {
        return type == null ? "EOF" : type + "(" + start + ", " + end + ")";
    }

    public boolean isNewLine() {
        return NEW_LINE.equals(type);
    }

    public boolean isEof() {
        return type == null;
    }

    public boolean isCode() {
        return !NON_CODE_TOKENS.contains(type) && !isEof();
    }

    public boolean isLet() {
        return LET.equals(type);
    }

    public boolean isIn() {
        return IN.equals(type);
    }

    public boolean isLeftBrace() {
        return LEFT_BRACE.equals(type);
    }

    public boolean isRightBrace() {
        return RIGHT_BRACE.equals(type);
    }

    public boolean isVirtual() {
        return VIRTUAL_TOKENS.contains(type);
    }

    public boolean isModuleStart() {
        return MODULE.equals(type) || PACKAGE.equals(type);
    }

    public boolean isProtectModifier() {
        return PROTECTED_MODIFIER.equals(type);
    }

    public boolean isFirstCodeTokenOnLine() {
        return isCode() &&
                line.columnWhereCodeStarts != null &&
                line.columnWhereCodeStarts == column;
    }

    public static class Line {
        public @Nullable Integer columnWhereCodeStarts;
    }
}
