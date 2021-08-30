package com.plugin.frege.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.plugin.frege.psi.FregeProgram
import com.plugin.frege.psi.impl.FregeProgramImpl
import com.plugin.frege.psi.mixin.FregeProgramUtil.imports
import com.plugin.frege.stubs.FregeProgramStub

class FregeProgramElementType(debugName: String) :
    FregeClassElementTypeAbstract<FregeProgramStub, FregeProgram>(debugName) {

    override fun createPsi(stub: FregeProgramStub): FregeProgram = FregeProgramImpl(stub, this)

    override fun serialize(stub: FregeProgramStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.name)
        dataStream.writeShort(stub.importStrings.size)
        for (import in stub.importStrings) {
            dataStream.writeUTFFast(import)
        }
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>): FregeProgramStub {
        val qualifiedName = dataStream.readName()
        val importsSize = dataStream.readShort()
        val importStrings = ArrayList<String>(importsSize.toInt())
        for (i in 0 until importsSize) {
            importStrings.add(dataStream.readUTFFast())
        }
        return FregeProgramStub(parentStub, this, qualifiedName, importStrings)
    }

    override fun createStub(psi: FregeProgram, parentStub: StubElement<out PsiElement>?): FregeProgramStub {
        val importStrings = psi.imports.map { it.text }
        return FregeProgramStub(parentStub, this, psi.qualifiedName, importStrings)
    }

    override fun getExternalId(): String = super.getExternalId() + ".PROGRAM"
}
