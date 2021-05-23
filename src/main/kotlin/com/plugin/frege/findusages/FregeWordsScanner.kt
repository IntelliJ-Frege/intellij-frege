package com.plugin.frege.findusages

import com.intellij.lang.cacheBuilder.WordOccurrence
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lexer.Lexer
import com.intellij.psi.tree.TokenSet
import com.intellij.util.Processor
import com.plugin.frege.lexer.FregeLexerAdapter
import com.plugin.frege.parser.FregeParserDefinition

class FregeWordsScanner : WordsScanner {
    override fun processWords(fileText: CharSequence, processor: Processor<in WordOccurrence>) {
        val lexer = FregeLexerAdapter()
        lexer.start(fileText)
        processWords(lexer, fileText, processor)
    }

    private fun processWords(lexer: Lexer, fileText: CharSequence, processor: Processor<in WordOccurrence>) {
        val identifiersOrCommentsOrLiterals = TokenSet.orSet(
            FregeParserDefinition.IDENTIFIERS,
            FregeParserDefinition.COMMENTS,
            FregeParserDefinition.STRING_LITERALS
        )

        var tokenType = lexer.tokenType
        while (tokenType != null) {
            when {
                identifiersOrCommentsOrLiterals.contains(tokenType) -> {
                    val occurrence = WordOccurrence(fileText, lexer.tokenStart, lexer.tokenEnd, WordOccurrence.Kind.CODE)
                    processor.process(occurrence)
                }
                FregeParserDefinition.OPERATORS.contains(tokenType) -> {
                    val start = lexer.tokenStart
                    var end = lexer.tokenEnd
                    while (true) {
                        lexer.advance()
                        tokenType = lexer.tokenType
                        if (tokenType == null || !FregeParserDefinition.OPERATORS.contains(tokenType)) {
                            break
                        }
                        end = lexer.tokenEnd
                    }

                    val occurrence = WordOccurrence(fileText, start, end, WordOccurrence.Kind.CODE)
                    processor.process(occurrence)

                    continue
                }
            }

            lexer.advance()
            tokenType = lexer.tokenType
        }
    }
}
