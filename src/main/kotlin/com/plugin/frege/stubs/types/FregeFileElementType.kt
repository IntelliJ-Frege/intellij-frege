package com.plugin.frege.stubs.types

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.psi.tree.IStubFileElementType
import com.plugin.frege.FregeLanguage
import com.plugin.frege.stubs.FregeFileStub
import org.jetbrains.annotations.NonNls

class FregeFileElementType(language: FregeLanguage?) : IStubFileElementType<FregeFileStub>(language) {
    override fun getStubVersion(): Int = VERSION

    override fun serialize(stub: FregeFileStub, dataStream: StubOutputStream) = Unit

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): FregeFileStub =
        FregeFileStub(null)

    @NonNls
    override fun getExternalId(): String = super.getExternalId() + ".FILE"

    companion object {
        @JvmField
        val INSTANCE = FregeFileElementType(FregeLanguage.INSTANCE)

        private const val VERSION = 4 // Change the version if you want to re-index Frege
    }
}
