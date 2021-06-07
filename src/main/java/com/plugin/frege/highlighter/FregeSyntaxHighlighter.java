package com.plugin.frege.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.plugin.frege.lexer.FregeLexerAdapter;
import com.plugin.frege.parser.FregeParserDefinition;
import com.plugin.frege.psi.FregeTypes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class FregeSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("FREGE_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("FREGE_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey CHAR =
            createTextAttributesKey("FREGE_CHAR", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("FREGE_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey TYPE =
            createTextAttributesKey("FREGE_TYPE", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey BRACKETS =
            createTextAttributesKey("FREGE_BRACKETS", DefaultLanguageHighlighterColors.CLASS_REFERENCE);
    public static final TextAttributesKey OPERATOR =
            createTextAttributesKey("FREGE_OPERATOR", DefaultLanguageHighlighterColors.INSTANCE_METHOD);
    public static final TextAttributesKey FUNCTION_NAME =
            createTextAttributesKey("FREGE_FUNCTION_NAME", DefaultLanguageHighlighterColors.INSTANCE_METHOD);
    public static final TextAttributesKey LINE_COMMENT =
            createTextAttributesKey("FREGE_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BLOCK_COMMENT =
            createTextAttributesKey("FREGE_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    public static final TextAttributesKey UNDEFINED =
            createTextAttributesKey("FREGE_UNDEFINED", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("FREGE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] CHAR_KEYS = new TextAttributesKey[]{CHAR};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] TYPE_KEYS = new TextAttributesKey[]{TYPE};
    private static final TextAttributesKey[] BRACKETS_KEYS = new TextAttributesKey[]{BRACKETS};
    private static final TextAttributesKey[] OPERATOR_KEYS = new TextAttributesKey[]{OPERATOR};
    private static final TextAttributesKey[] LINE_COMMENT_KEYS = new TextAttributesKey[]{LINE_COMMENT};
    private static final TextAttributesKey[] BLOCK_COMMENT_KEYS = new TextAttributesKey[]{BLOCK_COMMENT};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    static {
        UNDEFINED.getDefaultAttributes().setFontType(Font.ITALIC);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new FregeLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (FregeParserDefinition.KEYWORDS.contains(tokenType)) {
            return KEYWORD_KEYS;
        } else if (FregeParserDefinition.BRACKETS.contains(tokenType)) {
            return BRACKETS_KEYS;
        } else if (FregeParserDefinition.OPERATORS.contains(tokenType)) {
            return OPERATOR_KEYS;
        } else if (tokenType.equals(FregeTypes.INTEGER)) {
            return NUMBER_KEYS;
        } else if (tokenType.equals(FregeTypes.CHAR)) {
            return CHAR_KEYS;
        } else if (tokenType.equals(FregeTypes.STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(FregeTypes.CONID)) {
            return TYPE_KEYS;
        } else if (tokenType.equals(FregeTypes.LINE_COMMENT)) {
            return LINE_COMMENT_KEYS;
        } else if (tokenType.equals(FregeTypes.BLOCK_COMMENT)) {
            return BLOCK_COMMENT_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
