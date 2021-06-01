package com.plugin.frege.lexer.layout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

import static com.plugin.frege.lexer.layout.FregeLayoutLexerToken.createVirtualToken;
import static com.plugin.frege.psi.FregeTypes.*;

public class FregeLayoutLexerBlocksBuilder {
    private final @NotNull Stack<@NotNull Integer> indentStack = new Stack<>();
    private @NotNull FregeLayoutLexerBlock block = new FregeLayoutLexerBlock();
    private @Nullable FregeLayoutLexerToken newlineStickyToken;

    public FregeLayoutLexerBlocksBuilder() {
        indentStack.push(-1);
    }

    public boolean canFinishBlockWith(@NotNull FregeLayoutLexerToken token) {
        return token.isEof() || (token.isNewLine() && block.isContainsCode());
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
            block.add(token);
            newlineStickyToken = token;
        } else {
            if (indentStack.size() > 2) {
                FregeLayoutLexerToken precedes = getPrecedesToken();
                for (int i = 2; i < indentStack.size(); i++) {
                    block.add(createVirtualToken(VIRTUAL_END_SECTION, precedes));
                }
            }
            block.add(token);
        }

        FregeLayoutLexerBlock result = block;
        block = new FregeLayoutLexerBlock();
        return result;
    }

    public void add(@NotNull FregeLayoutLexerToken token) {
        block.add(token);
    }

    public void tryStartSectionWith(@NotNull FregeLayoutLexerToken token) {
        if (token.isLeftBrace()) {
            return;
        }
        if (token.column > indentStack.peek()) {
            add(createVirtualToken(VIRTUAL_OPEN_SECTION, getPrecedesToken()));
            indentStack.push(token.column);
        }
    }

    public boolean tryHandleSingleLineLetIn(@NotNull FregeLayoutLexerToken token) {
        if (token.isIn() && block.isContainsLet()) {
            add(createVirtualToken(VIRTUAL_END_SECTION, getPrecedesToken()));
            indentStack.pop();
            return true;
        }
        return false;
    }

    public void tryHandleSectionEndOrDeclEnd(@NotNull FregeLayoutLexerToken token) {
        if (token.isFirstCodeTokenOnLine()) {
            if (newlineStickyToken == null) {
                throw new FregeLayoutLexerException(
                        new IllegalStateException("Cannot find sticky newline token"));
            }
            while (token.column <= indentStack.peek()) {
                if (token.column == indentStack.peek()) {
                    block.addToVirtualPrefix(createVirtualToken(VIRTUAL_END_DECL, newlineStickyToken));
                    break;
                } else if (token.column < indentStack.peek()) {
                    block.addToVirtualPrefix(createVirtualToken(VIRTUAL_END_SECTION, newlineStickyToken));
                    indentStack.pop();
                }
            }
        }
    }

    public void insertFakeModuleSection() {
        indentStack.push(0);
    }
}
