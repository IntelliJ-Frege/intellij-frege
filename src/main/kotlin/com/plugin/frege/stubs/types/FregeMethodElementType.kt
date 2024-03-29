package com.plugin.frege.stubs.types

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.plugin.frege.psi.FregePsiMethod
import com.plugin.frege.stubs.FregeMethodStub
import com.plugin.frege.stubs.index.FregeMethodNameIndex

abstract class FregeMethodElementType(debugName: String) :
    FregeNamedStubElementType<FregeMethodStub, FregePsiMethod>(debugName) {

    override val nameKey by lazy { FregeMethodNameIndex.key }

    override fun createStub(psi: FregePsiMethod, parentStub: StubElement<*>?): FregeMethodStub =
        FregeMethodStub(parentStub, this, psi.name)

    override fun serialize(stub: FregeMethodStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.name)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>): FregeMethodStub {
        val name = dataStream.readName()
        return FregeMethodStub(parentStub, this, name)
    }

    override fun getExternalId(): String = super.getExternalId() + ".METHOD"
}
