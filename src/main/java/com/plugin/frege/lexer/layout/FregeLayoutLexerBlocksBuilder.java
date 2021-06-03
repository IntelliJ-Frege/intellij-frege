package com.plugin.frege.lexer.layout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.plugin.frege.lexer.layout.FregeLayoutLexerToken.createVirtualToken;
import static com.plugin.frege.psi.FregeTypes.*;

public class FregeLayoutLexerBlocksBuilder {
    private final @NotNull FregeLayoutLexerStack stack = new FregeLayoutLexerStack();
    private @NotNull FregeLayoutLexerBlock block = new FregeLayoutLexerBlock();
    private @Nullable FregeLayoutLexerToken newlineStickyToken;

    public boolean canFinishBlockWith(@NotNull FregeLayoutLexerToken token) {
        return token.isEof() || (token.isNewLine() && block.isContainsNotSkipping());
    }


    private @NotNull FregeLayoutLexerToken getPrecedesToken() {
        if (block.isMainTokensEmpty()) {
            if (newlineStickyToken == null) {
                throw new FregeLayoutLexerException(
                        new IllegalStateException("Cannot find precedes token"));
            }
            return newlineStickyToken;
        } else {
            return block.get(block.size() - 1);
        }
    }

    public @NotNull FregeLayoutLexerBlock finishBlock(@NotNull FregeLayoutLexerToken token) {
        if (!canFinishBlockWith(token)) {
            throw new FregeLayoutLexerException(
                    new IllegalStateException("Cannot finish block using token " + token));
        }
        if (token.isNewLine()) {
            add(token);
            newlineStickyToken = token;
        } else {
            int toBottom = stack.skipToBottom();
            if (toBottom > 1) {
                FregeLayoutLexerToken precedes = getPrecedesToken();
                for (int i = 0; i < toBottom - 1; i++) {
                    add(createVirtualToken(VIRTUAL_END_SECTION, precedes));
                }
            }
            add(token);
        }

        FregeLayoutLexerBlock result = block;
        block = new FregeLayoutLexerBlock();
        return result;
    }

    public void add(@NotNull FregeLayoutLexerToken token) {
        if (token.isLeftBrace()) {
            stack.enterLeftBrace(token.isSectionGenerating);
        }
        if (token.isRightBrace()) {
            int endedVirtualSections = stack.enterRightBrace();
            if (endedVirtualSections > 0) {
                FregeLayoutLexerToken precedes = getPrecedesToken();
                for (int i = 0; i < endedVirtualSections; i++) {
                    block.add(createVirtualToken(VIRTUAL_END_SECTION, precedes));
                }
            }
        }
        block.add(token);
    }

    public boolean tryStartSectionWith(@NotNull FregeLayoutLexerToken token) {
        if (token.isLeftBrace()) {
            token.isSectionGenerating = true;
            add(token);
            return true;
        }
        if (token.column > stack.getCurrentIndentLevel()) {
            add(createVirtualToken(VIRTUAL_OPEN_SECTION, getPrecedesToken()));
            stack.enterVirtualSectionStart(token.column);
            add(token);
            return true;
        }
        return false;
    }

    public boolean tryHandleSingleLineLetIn(@NotNull FregeLayoutLexerToken token) {
        if (token.isIn() && block.isContainsLet()) {
            add(createVirtualToken(VIRTUAL_END_SECTION, getPrecedesToken()));
            stack.enterVirtualSectionEnd();
            add(token);
            return true;
        }
        return false;
    }

    public boolean tryHandleSectionEndOrDeclEnd(@NotNull FregeLayoutLexerToken token) {
        if (token.isFirstNotSkippingTokenOnLine()) {
            if (newlineStickyToken == null) {
                throw new FregeLayoutLexerException(
                        new IllegalStateException("Cannot find sticky newline token"));
            }
            while (token.column <= stack.getCurrentIndentLevel()) {
                if (token.column == stack.getCurrentIndentLevel()) {
                    block.addToVirtualPrefix(createVirtualToken(VIRTUAL_END_DECL, newlineStickyToken));
                    break;
                } else if (token.column < stack.getCurrentIndentLevel()) {
                    block.addToVirtualPrefix(createVirtualToken(VIRTUAL_END_SECTION, newlineStickyToken));
                    stack.enterVirtualSectionEnd();
                }
            }
            add(token);
            return true;
        }
        return false;
    }
    public void globalSectionStart() {
        stack.enterVirtualSectionStart(-1);
    }
}
