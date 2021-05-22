package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.stubs.IStubElementType
import com.plugin.frege.psi.FregeDataDclNative
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.FregePsiClassHolder
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.stubs.FregeClassStub

@Suppress("UnstableApiUsage")
abstract class FregeDataDclNativeMixin : FregePsiClassImpl, FregePsiClassHolder, FregeDataDclNative {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeClassStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getHoldingClass(): FregePsiClass? {
        return this
    }

    override fun getName(): String {
        return dataNameUsage.text
    }

    override fun isInterface(): Boolean {
        return false
    }

    override fun getMethods(): Array<PsiMethod> {
        return PsiMethod.EMPTY_ARRAY
    }

    override fun getNameIdentifier(): PsiIdentifier {
        return dataNameUsage
    }

    override fun getScope(): PsiElement {
        return this
    }

    override fun setName(name: @NlsSafe String): PsiElement {
        return this // name identifier performs this
    }
}
