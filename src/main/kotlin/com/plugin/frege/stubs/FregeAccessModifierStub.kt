package com.plugin.frege.stubs

import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import com.plugin.frege.psi.FregeAccessModifier
import com.plugin.frege.psi.mixin.FregeAccessModifiers
import com.plugin.frege.stubs.types.FregeAccessModifierElementType

class FregeAccessModifierStub(
        parent: StubElement<*>?,
        elementType: FregeAccessModifierElementType,
        val modifier: FregeAccessModifiers
    ) : StubBase<FregeAccessModifier>(parent, elementType) {

    constructor(
        parent: StubElement<*>?,
        elementType: FregeAccessModifierElementType,
        modifierOrdinal: Int
    ) : this(parent, elementType, checkNotNull(FregeAccessModifiers.of(modifierOrdinal)))

    constructor(
        parent: StubElement<*>?,
        elementType: FregeAccessModifierElementType,
        modifierName: String
    ) : this(parent, elementType, checkNotNull(FregeAccessModifiers.of(modifierName)))

    val ordinal: Int = modifier.ordinal
    val name: String = modifier.toString()

    override fun toString(): String {
        return super.toString() + ".ACCESS_MODIFIER"
    }
}
