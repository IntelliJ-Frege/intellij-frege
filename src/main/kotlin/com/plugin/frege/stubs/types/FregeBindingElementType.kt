package com.plugin.frege.stubs.types

import com.intellij.lang.ASTNode
import com.plugin.frege.psi.FregeBinding
import com.plugin.frege.psi.FregeClassDecl
import com.plugin.frege.psi.FregeProgram
import com.plugin.frege.psi.FregePsiMethod
import com.plugin.frege.psi.impl.FregeBindingImpl
import com.plugin.frege.psi.util.FregePsiUtil.isInGlobalScope
import com.plugin.frege.stubs.FregeMethodStub

class FregeBindingElementType(debugName: String) : FregeMethodElementType(debugName) {
    override fun createPsi(stub: FregeMethodStub): FregePsiMethod {
        return FregeBindingImpl(stub, this)
    }

    override fun getExternalId(): String {
        return super.getExternalId() + ".BINDING"
    }

    override fun shouldCreateStub(node: ASTNode): Boolean {
        val element = node.psi
        if (element !is FregeBinding) {
            return false
        }
        val containingClass = element.containingClass
        return (containingClass !is FregeProgram && containingClass !is FregeClassDecl
                || isInGlobalScope(element)) && element.nameIdentifier?.reference?.resolve() === element
    }
}
