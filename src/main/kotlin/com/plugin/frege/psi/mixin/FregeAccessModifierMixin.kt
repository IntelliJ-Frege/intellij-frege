package com.plugin.frege.psi.mixin

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiModifier
import com.intellij.psi.stubs.IStubElementType
import com.plugin.frege.psi.FregeCompositeElement
import com.plugin.frege.stubs.FregeAccessModifierStub

open class FregeAccessModifierMixin : StubBasedPsiElementBase<FregeAccessModifierStub>, FregeCompositeElement {
    constructor(stub: FregeAccessModifierStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    constructor(node: ASTNode) : super(node)

    override fun toString(): String {
        return node.elementType.toString()
    }
}

enum class FregeAccessModifiers {
    Public {
        override val psiModifier: String = PsiModifier.PUBLIC
    },
    Protected {
        override val psiModifier: String = PsiModifier.PROTECTED
    },
    Private {
        override val psiModifier: String = PsiModifier.PRIVATE
    };

    abstract val psiModifier: String
    override fun toString(): String = super.toString().lowercase()

    companion object {
        fun of(name: String): FregeAccessModifiers? = values().firstOrNull { name.lowercase() == it.toString() }
        fun of(ordinal: Int): FregeAccessModifiers? = values().getOrNull(ordinal)
    }
}
