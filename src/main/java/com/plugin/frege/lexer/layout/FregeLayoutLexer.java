package com.plugin.frege.lexer.layout;

import com.plugin.frege.lexer.FregeLexerAdapter;
import org.jetbrains.annotations.NotNull;

public class FregeLayoutLexer {
    private final @NotNull FregeLayoutLexerBlocksProvider blockIterator;
    private @NotNull FregeLayoutLexerBlock currentBlock;
    private int currentBlockPos = 0;

    public FregeLayoutLexer(@NotNull FregeLexerAdapter lexer) {
        blockIterator = new FregeLayoutLexerBlocksProvider(lexer);
        currentBlock = blockIterator.next();
    }

    public FregeLayoutLexerToken getCurrentToken() {
        return currentBlock.get(currentBlockPos);
    }

    public void advance() {
        if (currentBlockPos + 1 < currentBlock.size()) {
            currentBlockPos++;
            return;
        }
        if (blockIterator.hasNext()) {
            currentBlock = blockIterator.next();
            currentBlockPos = 0;
        }
    }
}
