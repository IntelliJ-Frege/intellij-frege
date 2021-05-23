package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.impl.light.LightModifierList
import com.intellij.psi.stubs.IStubElementType
import com.plugin.frege.FregeLanguage
import com.plugin.frege.psi.FregeClassDcl
import com.plugin.frege.psi.FregeDecl
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.stubs.FregeClassStub

@Suppress("UnstableApiUsage")
abstract class FregeClassDclMixin : FregePsiClassImpl, FregeClassDcl {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeClassStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun setName(name: String): PsiElement {
        return this // TODO
    }

    override fun getName(): String {
        return nameIdentifier?.text ?: ""
    }

    override fun getNameIdentifier(): PsiIdentifier? {
        return constraints?.constraintList?.firstOrNull()?.className?.dataNameUsage
    }

    override fun getMethods(): Array<PsiMethod> {
        val subprograms = whereSection?.subprogramsFromScope ?: return PsiMethod.EMPTY_ARRAY
        return subprograms
            .mapNotNull { (it as? FregeDecl)?.annotation }
            .flatMap { it.annoItemList }
            .toTypedArray()
    }

    override fun isInterface(): Boolean {
        return true
    }

    override fun getScope(): PsiElement {
        return this
    }

    override fun getModifierList(): PsiModifierList {
        return LightModifierList(manager, FregeLanguage.INSTANCE, PsiModifier.PUBLIC, PsiModifier.ABSTRACT) // TODO
    }
}
