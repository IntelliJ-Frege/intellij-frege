package com.plugin.frege.stubs.types

import com.plugin.frege.psi.FregePsiMethod
import com.plugin.frege.psi.impl.FregeConstructImpl
import com.plugin.frege.stubs.FregeMethodStub

class FregeConstructElementType(debugName: String) : FregeMethodElementType(debugName) {
    override fun createPsi(stub: FregeMethodStub): FregePsiMethod = FregeConstructImpl(stub, this)

    override fun getExternalId(): String = super.getExternalId() + ".CONSTRUCT"
}
