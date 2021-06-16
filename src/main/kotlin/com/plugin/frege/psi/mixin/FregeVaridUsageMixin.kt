package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.plugin.frege.psi.FregeResolvableElement
import com.plugin.frege.psi.impl.FregeCompositeElementImpl
import com.plugin.frege.resolve.FregeVaridUsageReference

open class FregeVaridUsageMixin(node: ASTNode) : FregeCompositeElementImpl(node), FregeResolvableElement {
    override fun getReference(): FregeVaridUsageReference? {
        return FregeVaridUsageReference(this)
    }
}
