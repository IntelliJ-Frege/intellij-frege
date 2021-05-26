package com.plugin.frege.stubs.types

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubIndexKey
import com.intellij.psi.stubs.StubInputStream
import com.plugin.frege.psi.FregePsiMethod
import com.plugin.frege.stubs.FregeMethodStub
import com.plugin.frege.stubs.index.FregeMethodNameIndex

abstract class FregeMethodElementType(debugName: String) :
    FregeNamedStubElementType<FregeMethodStub, FregePsiMethod>(debugName) {

    override val key: StubIndexKey<String, FregePsiMethod>
        get() = FregeMethodNameIndex.INSTANCE.key

    override fun createStub(psi: FregePsiMethod, parentStub: StubElement<*>?): FregeMethodStub {
        return FregeMethodStub(parentStub, this, psi.name)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>): FregeMethodStub {
        val name = dataStream.readName()
        return FregeMethodStub(parentStub, this, name)
    }

    override fun getExternalId(): String {
        return super.getExternalId() + ".METHOD"
    }
}
