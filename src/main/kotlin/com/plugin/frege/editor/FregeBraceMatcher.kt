package com.plugin.frege.editor

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.plugin.frege.parser.FregeParserDefinition
import com.plugin.frege.psi.FregeTypes

class FregeBraceMatcher : PairedBraceMatcher {
    private val bracePairs = arrayOf(
        BracePair(FregeTypes.LEFT_PAREN, FregeTypes.RIGHT_PAREN, false),
        BracePair(FregeTypes.LEFT_BRACE, FregeTypes.RIGHT_BRACE, true),
        BracePair(FregeTypes.LEFT_BRACKET, FregeTypes.RIGHT_BRACKET, true)
    )

    override fun getPairs(): Array<BracePair> {
        return bracePairs
    }

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean {
        return !FregeParserDefinition.IDENTIFIERS.contains(contextType) &&
                !FregeParserDefinition.STRING_LITERALS.contains(contextType) &&
                contextType != FregeTypes.LEFT_PAREN &&
                contextType != FregeTypes.LEFT_BRACE &&
                contextType != FregeTypes.LEFT_BRACKET
    }

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int {
        return openingBraceOffset
    }
}
