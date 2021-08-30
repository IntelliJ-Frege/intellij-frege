package com.plugin.frege.stubs.types

import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.impl.FregeTypeDeclImpl
import com.plugin.frege.stubs.FregeClassStub

class FregeTypeDeclElementType(debugName: String) : FregeClassElementType(debugName) {
    override fun createPsi(stub: FregeClassStub): FregePsiClass = FregeTypeDeclImpl(stub, this)

    override fun getExternalId(): String = super.getExternalId() + ".TYPE"
}
