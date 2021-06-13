package com.plugin.frege.stubs.types

import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.NamedStubBase
import com.intellij.psi.stubs.StubIndexKey
import com.plugin.frege.psi.FregeNamedElement

abstract class FregeNamedStubElementType<StubT : NamedStubBase<*>, PsiT : FregeNamedElement?>(debugName: String) :
    FregeStubElementType<StubT, PsiT>(debugName) {

    protected abstract val nameKey: StubIndexKey<Int, in PsiT>

    override fun indexStub(stub: StubT, sink: IndexSink) {
        val name = stub.name
        if (name != null) {
            sink.occurrence(nameKey, name.hashCode())
        }
    }
}
