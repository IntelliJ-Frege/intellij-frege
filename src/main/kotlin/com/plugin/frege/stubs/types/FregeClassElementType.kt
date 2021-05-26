package com.plugin.frege.stubs.types

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubIndexKey
import com.intellij.psi.stubs.StubInputStream
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.stubs.FregeClassStub
import com.plugin.frege.stubs.index.FregeClassNameIndex

abstract class FregeClassElementType(debugName: String) :
    FregeNamedStubElementType<FregeClassStub, FregePsiClass>(debugName) {

    override val key: StubIndexKey<String, FregePsiClass>
        get() = FregeClassNameIndex.INSTANCE.key

    override fun createStub(psi: FregePsiClass, parentStub: StubElement<*>?): FregeClassStub {
        return FregeClassStub(parentStub, this, psi.qualifiedName)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>): FregeClassStub {
        val name = dataStream.readName()
        return FregeClassStub(parentStub, this, name)
    }

    override fun getExternalId(): String {
        return super.getExternalId() + ".CLASS"
    }
}
