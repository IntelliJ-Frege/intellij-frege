package com.plugin.frege.psi.impl

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.plugin.frege.FregeIcons
import com.plugin.frege.psi.FregeNamedElement
import javax.swing.Icon

abstract class FregeNamedStubBasedPsiElementBase<T : StubElement<*>> : StubBasedPsiElementBase<T>, FregeNamedElement {
    constructor(stub: T, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    constructor(node: ASTNode) : super(node)

    /**
     * Returns `true` if this element can be used as a resolved reference.
     */
    open val canBeReferenced: Boolean = true

    override fun setName(name: String): PsiElement {
        nameIdentifier?.reference?.handleElementRename(name)
        return this
    }

    override fun getNavigationElement(): PsiElement {
        return nameIdentifier ?: this
    }

    override fun getTextOffset(): Int {
        val nameIdentifier = nameIdentifier
        return if (nameIdentifier != null && nameIdentifier !== this) {
            nameIdentifier.textOffset
        } else {
            super.getTextOffset()
        }
    }

    override fun toString(): String {
        return node.elementType.toString()
    }

    override fun getIcon(flags: Int): Icon? {
        return FregeIcons.FILE
    }
}
