package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.stubs.IStubElementType
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.FregeTypeDecl
import com.plugin.frege.psi.FregeTypedVarid
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.psi.util.FregePsiUtil.findMainTypeFromSigma
import com.plugin.frege.resolve.FregeResolveUtil
import com.plugin.frege.stubs.FregeClassStub

abstract class FregeTypeDeclMixin : FregePsiClassImpl<FregeClassStub>, FregeTypeDecl {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeClassStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameWithoutStub(): String = nameIdentifier?.text ?: DEFAULT_CLASS_NAME

    override fun getNameIdentifier(): PsiIdentifier? = conidUsage

    override fun getMethods(): Array<PsiMethod> {
        val alias = findMainTypeFromSigma(sigma) ?: return PsiMethod.EMPTY_ARRAY
        val classes = FregeResolveUtil.findClassesFromUsage(alias, false)
        if (classes.size != 1) {
            return PsiMethod.EMPTY_ARRAY
        }
        val aliasClass = classes.first() as? FregePsiClass
        return aliasClass?.methods ?: PsiMethod.EMPTY_ARRAY
    }

    override fun generateDoc(): String = generateDoc("Type", "Alias")

    override fun isInterface(): Boolean = false

    override fun getScope(): PsiElement = this

    override val typedVaridDeclarations get(): List<FregeTypedVarid> = typedVaridList
}
