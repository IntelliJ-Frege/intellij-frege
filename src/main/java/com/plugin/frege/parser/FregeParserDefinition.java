package com.plugin.frege.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.plugin.frege.FregeLanguage;
import com.plugin.frege.lexer.FregeLayoutLexer;
import com.plugin.frege.psi.FregeFile;
import com.plugin.frege.psi.FregeTypes;
import org.jetbrains.annotations.NotNull;

public class FregeParserDefinition implements ParserDefinition {

    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(FregeTypes.LINE_COMMENT, FregeTypes.BLOCK_COMMENT);
    public static final TokenSet STRING_LITERALS = TokenSet.create(FregeTypes.STRING, FregeTypes.CHAR);
    public static final TokenSet BRACKETS = TokenSet.create(FregeTypes.LEFT_BRACKET, FregeTypes.RIGHT_BRACKET);
    public static final TokenSet IDENTIFIERS = TokenSet.create(FregeTypes.CONID, FregeTypes.VARID);

    public static final TokenSet OPERATORS = TokenSet.create(FregeTypes.WORD_OPERATOR, FregeTypes.DOUBLE_COLON,
            FregeTypes.COLON, FregeTypes.RIGHT_ARROW, FregeTypes.LEFT_ARROW, FregeTypes.DOUBLE_RIGHT_ARROW,
            FregeTypes.VERT_BAR, FregeTypes.EQUAL, FregeTypes.DASH, FregeTypes.EXLAMATION_MARK, FregeTypes.PLUS,
            FregeTypes.QUESTION_MARK, FregeTypes.COMMA, FregeTypes.SEMICOLON, FregeTypes.SLASH, FregeTypes.DOT,
            FregeTypes.BACK_SLASH, FregeTypes.STAR, FregeTypes.AT, FregeTypes.TILDA, FregeTypes.HASH,
            FregeTypes.LESS, FregeTypes.GREATER);

    public static final TokenSet KEYWORDS = TokenSet.create(FregeTypes.ABSTRACT, FregeTypes.AS, FregeTypes.CASE, FregeTypes.CLASS,
            FregeTypes.INTERFACE, FregeTypes.DATA, FregeTypes.DERIVE, FregeTypes.DO, FregeTypes.ELSE,
            FregeTypes.FALSE, FregeTypes.FORALL, FregeTypes.HIDING, FregeTypes.IF, FregeTypes.IMPORT, FregeTypes.IN, FregeTypes.INFIX,
            FregeTypes.INFIXL, FregeTypes.INFIXR, FregeTypes.INLINE, FregeTypes.INSTANCE, FregeTypes.LET,
            FregeTypes.MUTABLE, FregeTypes.NATIVE, FregeTypes.OF, FregeTypes.PACKAGE, FregeTypes.MODULE,
            FregeTypes.PRIVATE_MODIFIER, FregeTypes.PROTECTED_MODIFIER, FregeTypes.PURE, FregeTypes.PUBLIC_MODIFIER, FregeTypes.THEN,
            FregeTypes.THROWS, FregeTypes.TRUE, FregeTypes.TYPE, FregeTypes.WHERE);

    public static final IFileElementType FILE = new IFileElementType(FregeLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new FregeLayoutLexer();
    }

    @Override
    public PsiParser createParser(Project project) {
        return (root, builder) -> {
            builder.setTokenTypeRemapper((source, start, end, text) -> {
                if (source == FregeTypes.NEW_LINE) {
                    return TokenType.WHITE_SPACE;
                } else {
                    return source;
                }
            });
            return new FregeParser().parse(root, builder);
        };
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return STRING_LITERALS;
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return FregeTypes.Factory.createElement(node);
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new FregeFile(viewProvider);
    }

    @Override
    public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
