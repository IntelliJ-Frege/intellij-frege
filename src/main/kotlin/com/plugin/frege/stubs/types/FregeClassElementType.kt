package com.plugin.frege.stubs.types

import com.intellij.psi.stubs.*
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.impl.FregePsiUtilImpl.nameFromQualifiedName
import com.plugin.frege.stubs.FregeClassStub
import com.plugin.frege.stubs.index.FregeClassNameIndex
import com.plugin.frege.stubs.index.FregeShortClassNameIndex

abstract class FregeClassElementType(debugName: String) :
    FregeNamedStubElementType<FregeClassStub, FregePsiClass>(debugName) {

    override val nameKey = FregeClassNameIndex.INSTANCE.key

    protected val shortNameKey = FregeShortClassNameIndex.INSTANCE.key

    override fun indexStub(stub: FregeClassStub, sink: IndexSink) {
        super.indexStub(stub, sink)
        val qualifiedName = stub.name ?: return
        val shortName = nameFromQualifiedName(qualifiedName)
        sink.occurrence(shortNameKey, shortName.hashCode())
    }

    override fun createStub(psi: FregePsiClass, parentStub: StubElement<*>?): FregeClassStub {
        return FregeClassStub(parentStub, this, psi.qualifiedName)
    }

    override fun serialize(stub: FregeClassStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.name)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>): FregeClassStub {
        val qualifiedName = dataStream.readName()
        return FregeClassStub(parentStub, this, qualifiedName)
    }

    override fun getExternalId(): String {
        return super.getExternalId() + ".CLASS"
    }
}
