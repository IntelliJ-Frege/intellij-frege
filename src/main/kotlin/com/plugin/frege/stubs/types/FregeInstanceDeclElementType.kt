package com.plugin.frege.stubs.types

import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.impl.FregeInstanceDeclImpl
import com.plugin.frege.stubs.FregeClassStub

class FregeInstanceDeclElementType(debugName: String) : FregeClassElementType(debugName) {
    override fun createPsi(stub: FregeClassStub): FregePsiClass {
        return FregeInstanceDeclImpl(stub, this)
    }

    override fun getExternalId(): String {
        return super.getExternalId() + "INSTANCE"
    }
}
