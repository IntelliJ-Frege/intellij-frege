package com.plugin.frege.stubs.types

import com.intellij.lang.Language
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.plugin.frege.FregeLanguage
import com.plugin.frege.psi.FregeCompositeElement
import org.jetbrains.annotations.NonNls

abstract class FregeStubElementType<StubT : StubElement<*>, PsiT : FregeCompositeElement?>(@NonNls debugName: String) :
    IStubElementType<StubT, PsiT>(debugName, FregeLanguage.INSTANCE) {

    override fun getLanguage(): Language = FregeLanguage.INSTANCE

    override fun getExternalId(): String = "frege." + super.toString()
}
