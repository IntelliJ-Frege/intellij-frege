package com.plugin.frege.stubs.types

import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.impl.FregeProgramImpl
import com.plugin.frege.stubs.FregeClassStub

class FregeProgramElementType(debugName: String) : FregeClassElementType(debugName) {
    override fun createPsi(stub: FregeClassStub): FregePsiClass {
        return FregeProgramImpl(stub, this)
    }

    override fun getExternalId(): String {
        return super.getExternalId() + ".PROGRAM"
    }
}
