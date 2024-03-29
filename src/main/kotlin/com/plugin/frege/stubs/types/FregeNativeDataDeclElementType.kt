package com.plugin.frege.stubs.types

import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.impl.FregeNativeDataDeclImpl
import com.plugin.frege.stubs.FregeClassStub

class FregeNativeDataDeclElementType(debugName: String) : FregeClassElementType(debugName) {
    override fun createPsi(stub: FregeClassStub): FregePsiClass = FregeNativeDataDeclImpl(stub, this)

    override fun getExternalId(): String = super.getExternalId() + ".NATIVE_DATA"
}
