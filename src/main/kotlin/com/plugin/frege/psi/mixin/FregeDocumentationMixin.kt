package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.plugin.frege.psi.FregeDocumentation
import com.plugin.frege.psi.FregeDocumentationElement
import com.plugin.frege.psi.impl.FregeCompositeElementImpl

abstract class FregeDocumentationMixin(node: ASTNode) :
    FregeCompositeElementImpl(node), FregeDocumentationElement, FregeDocumentation {

    private val lineCommentPrefixLen = 3
    private val blockCommentPrefixLen = 3
    private val blockCommentSuffixLen = 2

    override fun getDocumentationText(): String {
        lineDocumentation?.let {
            return it.text.drop(lineCommentPrefixLen)
        }
        
        return blockDocumentation
            ?.text
            ?.drop(blockCommentPrefixLen)
            ?.dropLast(blockCommentSuffixLen) ?: ""
    }
}
