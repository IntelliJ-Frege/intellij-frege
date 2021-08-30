package com.plugin.frege.stubs.types

import com.intellij.psi.stubs.IndexSink
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.util.FregePsiUtil
import com.plugin.frege.stubs.FregeClassStub
import com.plugin.frege.stubs.index.FregeClassNameIndex
import com.plugin.frege.stubs.index.FregeShortClassNameIndex

abstract class FregeClassElementTypeAbstract<StubT : FregeClassStub, PsiT : FregePsiClass>(debugName: String) :
    FregeNamedStubElementType<StubT, PsiT>(debugName) {

    override val nameKey by lazy { FregeClassNameIndex.key }

    protected val shortNameKey by lazy { FregeShortClassNameIndex.key }

    override fun indexStub(stub: StubT, sink: IndexSink) {
        super.indexStub(stub, sink)
        val qualifiedName = stub.name ?: return
        val shortName = FregePsiUtil.nameFromQualifiedName(qualifiedName)
        sink.occurrence(shortNameKey, shortName.hashCode())
    }

    override fun getExternalId(): String = super.getExternalId() + ".CLASS"
}
