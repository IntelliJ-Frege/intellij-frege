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
import com.plugin.frege.lexer.FregeLexerAdapter;
import com.plugin.frege.psi.FregeFile;
import com.plugin.frege.psi.FregeTypes;
import org.jetbrains.annotations.NotNull;

public class FregeParserDefinition implements ParserDefinition {

    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(); // HS_COMMENT, HS_NCOMMENT, HS_HADDOCK, HS_NHADDOCK
    public static final TokenSet STRING_LITERALS = TokenSet.create(); // HS_CHARACTER_LITERAL, HS_STRING_LITERAL

    public static final IFileElementType FILE = new IFileElementType(FregeLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new FregeLexerAdapter();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new FregeParser();
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
