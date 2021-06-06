package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.plugin.frege.psi.FregeDocumentation
import com.plugin.frege.psi.FregeDocumentationElement
import com.plugin.frege.psi.impl.FregeCompositeElementImpl

abstract class FregeDocumentationMixin(node: ASTNode) : FregeCompositeElementImpl(node),
    FregeDocumentationElement, FregeDocumentation {
    override fun getDocumentationText(): String {
        return lineDocumentation?.text?.drop(3) ?: blockDocumentation?.text?.drop(3)?.dropLast(2) ?: ""
    }
}
