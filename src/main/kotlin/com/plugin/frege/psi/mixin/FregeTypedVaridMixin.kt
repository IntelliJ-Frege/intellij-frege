package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.plugin.frege.psi.FregeTypedVarid
import com.plugin.frege.psi.impl.FregeNamedElementImpl

abstract class FregeTypedVaridMixin(node: ASTNode) : FregeNamedElementImpl(node), FregeTypedVarid {
    override fun setName(name: String): PsiElement = apply {
        nameIdentifier?.reference?.handleElementRename(name)
    }

    override fun getNameIdentifier(): PsiElement? = typeParameter
}
