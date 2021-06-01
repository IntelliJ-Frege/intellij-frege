package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.stubs.IStubElementType
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.FregeTypeDecl
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.psi.impl.FregePsiUtilImpl.findMainTypeFromSigma
import com.plugin.frege.stubs.FregeClassStub

abstract class FregeTypeDeclMixin : FregePsiClassImpl, FregeTypeDecl {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeClassStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameWithoutStub(): String {
        return nameIdentifier?.text ?: text
    }

    override fun setName(name: String): PsiElement {
        return this
    }

    override fun getNameIdentifier(): PsiIdentifier? {
        return conidUsage
    }

    override fun getMethods(): Array<PsiMethod> {
        val aliasClass = findMainTypeFromSigma(sigma)?.reference?.resolve() as? FregePsiClass
        return aliasClass?.methods ?: PsiMethod.EMPTY_ARRAY
    }

    override fun isInterface(): Boolean {
        return false
    }

    override fun getScope(): PsiElement {
        return this
    }
}
