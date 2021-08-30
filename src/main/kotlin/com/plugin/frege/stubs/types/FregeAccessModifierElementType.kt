package com.plugin.frege.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.plugin.frege.psi.FregeAccessModifier
import com.plugin.frege.psi.impl.FregeAccessModifierImpl
import com.plugin.frege.stubs.FregeAccessModifierStub

class FregeAccessModifierElementType(debugName: String) :
    FregeStubElementType<FregeAccessModifierStub, FregeAccessModifier>(debugName) {

    override fun serialize(stub: FregeAccessModifierStub, dataStream: StubOutputStream) {
        dataStream.writeShort(stub.ordinal)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): FregeAccessModifierStub {
        val ordinal = dataStream.readShort()
        return FregeAccessModifierStub(parentStub, this, ordinal.toInt())
    }

    override fun indexStub(stub: FregeAccessModifierStub, sink: IndexSink) = Unit

    override fun createPsi(stub: FregeAccessModifierStub): FregeAccessModifier = FregeAccessModifierImpl(stub, this)

    override fun createStub(
        psi: FregeAccessModifier,
        parentStub: StubElement<out PsiElement>?
    ): FregeAccessModifierStub = FregeAccessModifierStub(parentStub, this, psi.text)
}
