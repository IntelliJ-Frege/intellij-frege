package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.stubs.IStubElementType
import com.plugin.frege.psi.FregeDataDecl
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.stubs.FregeClassStub

@Suppress("UnstableApiUsage")
abstract class FregeDataDeclMixin : FregePsiClassImpl, FregeDataDecl {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeClassStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameWithoutStub(): @NlsSafe String {
        return nameIdentifier.text
    }

    override fun setName(name: String): PsiElement {
        return this
    }

    override fun getNameIdentifier(): PsiIdentifier {
        return conidUsage
    }

    override fun getMethods(): Array<PsiMethod> {
        return constructs.constructList.toTypedArray()
    }

    override fun isInterface(): Boolean {
        return false
    }

    override fun getScope(): PsiElement {
        return this
    }
}
