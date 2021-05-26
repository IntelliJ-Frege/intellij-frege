package com.plugin.frege.stubs.types

import com.intellij.lang.ASTNode
import com.plugin.frege.psi.FregeClassDecl
import com.plugin.frege.psi.FregePsiMethod
import com.plugin.frege.psi.impl.FregeAnnotationItemImpl
import com.plugin.frege.stubs.FregeMethodStub

class FregeAnnotationItemElementType(debugName: String) : FregeMethodElementType(debugName) {
    override fun createPsi(stub: FregeMethodStub): FregePsiMethod {
        return FregeAnnotationItemImpl(stub, this)
    }

    override fun getExternalId(): String {
        return super.getExternalId() + ".ANNOTATION_ITEM"
    }

    override fun shouldCreateStub(node: ASTNode): Boolean {
        val element = node.psi as? FregeAnnotationItemImpl ?: return true
        return element.containingClass is FregeClassDecl
    }
}
