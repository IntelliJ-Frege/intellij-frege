package com.plugin.frege.stubs.types

import com.plugin.frege.psi.FregePsiMethod
import com.plugin.frege.psi.impl.FregeLabelImpl
import com.plugin.frege.stubs.FregeMethodStub

class FregeLabelElementType(debugName: String) : FregeMethodElementType(debugName) {
    override fun createPsi(stub: FregeMethodStub): FregePsiMethod {
        return FregeLabelImpl(stub, this)
    }

    override fun getExternalId(): String {
        return super.getExternalId() + ".LABEL"
    }
}
