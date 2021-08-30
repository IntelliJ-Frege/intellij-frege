package com.plugin.frege.stubs.types

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.stubs.FregeClassStub

abstract class FregeClassElementType(debugName: String) :
    FregeClassElementTypeAbstract<FregeClassStub, FregePsiClass>(debugName) {

    override fun createStub(psi: FregePsiClass, parentStub: StubElement<*>?): FregeClassStub =
        FregeClassStub(parentStub, this, psi.qualifiedName)

    override fun serialize(stub: FregeClassStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.name)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>): FregeClassStub {
        val qualifiedName = dataStream.readName()
        return FregeClassStub(parentStub, this, qualifiedName)
    }
}
