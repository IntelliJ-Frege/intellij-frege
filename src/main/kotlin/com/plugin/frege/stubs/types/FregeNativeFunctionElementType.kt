package com.plugin.frege.stubs.types

import com.plugin.frege.psi.FregePsiMethod
import com.plugin.frege.psi.impl.FregeNativeFunctionImpl
import com.plugin.frege.stubs.FregeMethodStub

class FregeNativeFunctionElementType(debugName: String) : FregeMethodElementType(debugName) {
    override fun createPsi(stub: FregeMethodStub): FregePsiMethod {
        return FregeNativeFunctionImpl(stub, this)
    }

    override fun getExternalId(): String {
        return super.getExternalId() + ".NATIVE_FUNCTION"
    }
}
