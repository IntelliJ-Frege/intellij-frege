package com.plugin.frege.stubs.types

import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.NamedStubBase
import com.intellij.psi.stubs.StubIndexKey
import com.intellij.psi.stubs.StubOutputStream
import com.plugin.frege.psi.FregeNamedElement

abstract class FregeNamedStubElementType<StubT : NamedStubBase<*>, PsiT : FregeNamedElement?>(debugName: String) :
    FregeStubElementType<StubT, PsiT>(debugName) {

    protected abstract val key: StubIndexKey<String, PsiT>

    override fun serialize(stub: StubT, dataStream: StubOutputStream) {
        dataStream.writeName(stub.name)
    }

    override fun indexStub(stub: StubT, sink: IndexSink) {
        val name = stub.name
        if (name != null) {
            sink.occurrence(key, name)
        }
    }
}
