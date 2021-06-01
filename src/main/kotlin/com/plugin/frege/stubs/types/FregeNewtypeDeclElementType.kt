package com.plugin.frege.stubs.types

import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.impl.FregeNewtypeDeclImpl
import com.plugin.frege.stubs.FregeClassStub

class FregeNewtypeDeclElementType(debugName: String) : FregeClassElementType(debugName) {
    override fun createPsi(stub: FregeClassStub): FregePsiClass {
        return FregeNewtypeDeclImpl(stub, this)
    }

    override fun getExternalId(): String {
        return super.getExternalId() + ".NEWTYPE"
    }
}
