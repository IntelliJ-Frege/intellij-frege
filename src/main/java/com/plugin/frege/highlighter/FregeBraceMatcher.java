package com.plugin.frege.highlighter;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.plugin.frege.parser.FregeParserDefinition;
import com.plugin.frege.psi.FregeTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FregeBraceMatcher implements PairedBraceMatcher {
    private final BracePair[] BRACE_PAIRS = {
            new BracePair(FregeTypes.LEFT_PAREN, FregeTypes.RIGHT_PAREN, false),
            new BracePair(FregeTypes.LEFT_BRACE, FregeTypes.RIGHT_BRACE, true),
            new BracePair(FregeTypes.LEFT_BRACKET, FregeTypes.RIGHT_BRACKET, true)
    };


    @Override
    public BracePair @NotNull [] getPairs() {
        return BRACE_PAIRS;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return !FregeParserDefinition.IDENTIFIERS.contains(contextType) &&
                !FregeParserDefinition.STRING_LITERALS.contains(contextType) && contextType != FregeTypes.LEFT_PAREN &&
                contextType != FregeTypes.LEFT_BRACE && contextType != FregeTypes.LEFT_BRACKET;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
