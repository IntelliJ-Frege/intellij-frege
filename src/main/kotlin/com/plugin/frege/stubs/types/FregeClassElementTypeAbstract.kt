package com.plugin.frege.stubs.types

import com.intellij.psi.stubs.IndexSink
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.impl.FregePsiUtilImpl
import com.plugin.frege.stubs.FregeClassStub
import com.plugin.frege.stubs.index.FregeClassNameIndex
import com.plugin.frege.stubs.index.FregeShortClassNameIndex

abstract class FregeClassElementTypeAbstract<StubT : FregeClassStub, PsiT : FregePsiClass>(debugName: String) :
    FregeNamedStubElementType<StubT, PsiT>(debugName) {

    override val nameKey
        get() = FregeClassNameIndex.INSTANCE.key

    protected val shortNameKey
        get() = FregeShortClassNameIndex.INSTANCE.key

    override fun indexStub(stub: StubT, sink: IndexSink) {
        super.indexStub(stub, sink)
        val qualifiedName = stub.name ?: return
        val shortName = FregePsiUtilImpl.nameFromQualifiedName(qualifiedName)
        sink.occurrence(shortNameKey, shortName.hashCode())
    }

    override fun getExternalId(): String {
        return super.getExternalId() + ".CLASS"
    }
}
