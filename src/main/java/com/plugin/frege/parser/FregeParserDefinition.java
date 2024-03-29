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
import com.plugin.frege.lexer.layout.FregeLayoutLexerAdapter;
import com.plugin.frege.psi.FregeFile;
import com.plugin.frege.psi.FregeTypes;
import com.plugin.frege.stubs.types.FregeFileElementType;
import org.jetbrains.annotations.NotNull;

public class FregeParserDefinition implements ParserDefinition {

    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(FregeTypes.LINE_COMMENT, FregeTypes.BLOCK_COMMENT);
    public static final TokenSet DOCUMENTATION = TokenSet.create(FregeTypes.LINE_DOC, FregeTypes.BLOCK_DOC);
    public static final TokenSet STRING_LITERALS = TokenSet.create(FregeTypes.STRING, FregeTypes.CHAR);
    public static final TokenSet BRACKETS = TokenSet.create(FregeTypes.LEFT_BRACKET, FregeTypes.RIGHT_BRACKET);
    public static final TokenSet IDENTIFIERS = TokenSet.create(FregeTypes.CONID, FregeTypes.VARID, FregeTypes.AS,
            FregeTypes.HIDING, FregeTypes.INLINE, FregeTypes.MUTABLE, FregeTypes.PURE);

    public static final TokenSet OPERATORS = TokenSet.create(FregeTypes.BACK_QUOTE, FregeTypes.DOUBLE_COLON,
            FregeTypes.COLON, FregeTypes.RIGHT_ARROW, FregeTypes.LEFT_ARROW, FregeTypes.DOUBLE_RIGHT_ARROW,
            FregeTypes.VERTICAL_BAR, FregeTypes.EQUAL, FregeTypes.EXLAMATION_MARK,
            FregeTypes.QUESTION_MARK, FregeTypes.COMMA, FregeTypes.SEMICOLON, FregeTypes.DOT,
            FregeTypes.BACK_SLASH, FregeTypes.STAR, FregeTypes.AT, FregeTypes.TILDA,
            FregeTypes.SYMOP_NO_RESERVED);

    public static final TokenSet KEYWORDS = TokenSet.create(FregeTypes.ABSTRACT, FregeTypes.CASE, FregeTypes.CLASS,
            FregeTypes.INTERFACE, FregeTypes.DATA, FregeTypes.DERIVE, FregeTypes.DO, FregeTypes.ELSE,
            FregeTypes.FALSE, FregeTypes.FORALL, FregeTypes.IF, FregeTypes.IMPORT, FregeTypes.IN, FregeTypes.INFIX,
            FregeTypes.INFIXL, FregeTypes.INFIXR, FregeTypes.INSTANCE, FregeTypes.LET,
            FregeTypes.NATIVE, FregeTypes.NEWTYPE, FregeTypes.OF, FregeTypes.PACKAGE, FregeTypes.MODULE,
            FregeTypes.PRIVATE_MODIFIER, FregeTypes.PROTECTED_MODIFIER, FregeTypes.PUBLIC_MODIFIER, FregeTypes.THEN,
            FregeTypes.THROWS, FregeTypes.TRUE, FregeTypes.TYPE, FregeTypes.WHERE, FregeTypes.SUPER_OR_SUBSCRIPT);

    public static final TokenSet NUMBERS = TokenSet.create(FregeTypes.INTEGER, FregeTypes.FLOAT);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new FregeLayoutLexerAdapter();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
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
    public @NotNull IFileElementType getFileNodeType() {
        return FregeFileElementType.INSTANCE;
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
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new FregeFile(viewProvider);
    }

    @Override
    public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
