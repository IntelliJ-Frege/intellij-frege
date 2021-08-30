package com.plugin.frege.psi.impl

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.FregeIcons
import com.plugin.frege.psi.FregeAccessModifier
import com.plugin.frege.psi.FregeNamedElement
import com.plugin.frege.psi.mixin.FregeAccessModifiers
import com.plugin.frege.stubs.FregeAccessModifierStub
import javax.swing.Icon

abstract class FregeNamedStubBasedPsiElementBase<T : StubElement<*>> : StubBasedPsiElementBase<T>, FregeNamedElement {
    constructor(stub: T, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    constructor(node: ASTNode) : super(node)

    override fun setName(name: String): PsiElement = apply {
        nameIdentifier?.reference?.handleElementRename(name)
    }

    override fun getNavigationElement(): PsiElement = nameIdentifier ?: this

    override fun getTextOffset(): Int {
        val nameIdentifier = nameIdentifier
        return if (nameIdentifier != null && nameIdentifier !== this) {
            nameIdentifier.textOffset
        } else {
            super.getTextOffset()
        }
    }

    override fun toString(): String = node.elementType.toString()

    override fun getIcon(flags: Int): Icon? = FregeIcons.FILE

    val accessModifiers: FregeAccessModifiers
        get() {
            val greenStub = greenStub
            val result = if (greenStub != null) {
                greenStub.childrenStubs.filterIsInstance<FregeAccessModifierStub>().firstOrNull()?.modifier
            } else {
                PsiTreeUtil.getChildOfType(this, FregeAccessModifier::class.java)?.let {
                    FregeAccessModifiers.of(it.text)
                }
            }
            return result ?: FregeAccessModifiers.Public
        }

    val accessPsiModifier: String
        get() = accessModifiers.psiModifier
}
