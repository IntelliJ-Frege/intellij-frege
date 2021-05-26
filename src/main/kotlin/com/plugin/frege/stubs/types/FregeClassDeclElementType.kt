package com.plugin.frege.stubs.types

import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.impl.FregeClassDeclImpl
import com.plugin.frege.stubs.FregeClassStub

class FregeClassDeclElementType(debugName: String) : FregeClassElementType(debugName) {
    override fun createPsi(stub: FregeClassStub): FregePsiClass {
        return FregeClassDeclImpl(stub, this)
    }

    override fun getExternalId(): String {
        return super.getExternalId() + ".CLASS_DECL"
    }
}
