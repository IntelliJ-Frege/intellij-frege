package com.plugin.frege.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext

class FregeKeywordCompletionProvider(private val keywords: List<String>, private val addSpaceToEnd: Boolean) :
    CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        result.addAllElements(keywords.map { keyword ->
            LookupElementBuilder.create(if (addSpaceToEnd) "$keyword " else keyword)
        })
    }
}
