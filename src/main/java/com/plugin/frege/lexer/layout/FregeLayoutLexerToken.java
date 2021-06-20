package com.plugin.frege.lexer.layout;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.plugin.frege.parser.FregeParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.plugin.frege.parser.FregeParserDefinition.COMMENTS;
import static com.plugin.frege.parser.FregeParserDefinition.WHITE_SPACES;
import static com.plugin.frege.psi.FregeTypes.*;

public class FregeLayoutLexerToken {
    private static final @NotNull TokenSet SKIPPING_TOKENS = TokenSet.orSet(COMMENTS, WHITE_SPACES, TokenSet.create(NEW_LINE));
    private static final @NotNull TokenSet DOCUMENTATION_TOKENS = FregeParserDefinition.DOCUMENTATION;
    private static final @NotNull TokenSet VIRTUAL_TOKENS = TokenSet.create(VIRTUAL_OPEN_SECTION, VIRTUAL_END_DECL, VIRTUAL_END_SECTION);
    public final @Nullable IElementType type;
    public final int start;
    public final int end;
    public final int column;
    public final @NotNull String text;
    public final @NotNull Line line;

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
        return new FregeLayoutLexerToken(type, precedesToken.end,
                precedesToken.end, precedesToken.column, "", precedesToken.line);
    }

    @Override
    public String toString() {
        return type == null ? "EOF" : type + "(" + start + ", " + end + ")";
    }

    public boolean isEof() {
        return type == null;
    }

    public boolean isType(@NotNull IElementType elementType) {
        return elementType.equals(type);
    }

    public boolean isSkipping() {
        return SKIPPING_TOKENS.contains(type);
    }

    public boolean isDocumentation() {
        return DOCUMENTATION_TOKENS.contains(type);
    }

    public boolean isCode() {
        return !isSkipping() && !isDocumentation() && !isEof();
    }

    public boolean isVirtual() {
        return VIRTUAL_TOKENS.contains(type);
    }

    public boolean isModuleStart() {
        return isType(MODULE) || isType(PACKAGE);
    }

    public boolean isFirstNotSkippingTokenOnLine() {
        return !isSkipping() &&
                line.columnWhereNotSkippingStarts != null &&
                line.columnWhereNotSkippingStarts == column;
    }

    public static class Line {
        public @Nullable Integer columnWhereNotSkippingStarts;
    }
}
