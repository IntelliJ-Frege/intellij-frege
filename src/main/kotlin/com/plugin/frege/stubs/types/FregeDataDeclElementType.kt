package com.plugin.frege.stubs.types

import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.impl.FregeDataDeclImpl
import com.plugin.frege.stubs.FregeClassStub

class FregeDataDeclElementType(debugName: String) : FregeClassElementType(debugName) {
    override fun createPsi(stub: FregeClassStub): FregePsiClass = FregeDataDeclImpl(stub, this)

    override fun getExternalId(): String = super.getExternalId() + ".DATA"
}
