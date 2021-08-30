package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.stubs.IStubElementType
import com.plugin.frege.psi.FregeDataDecl
import com.plugin.frege.psi.FregeTypedVarid
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.stubs.FregeClassStub

@Suppress("UnstableApiUsage")
abstract class FregeDataDeclMixin : FregePsiClassImpl<FregeClassStub>, FregeDataDecl {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeClassStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameWithoutStub(): String = nameIdentifier.text

    override fun getNameIdentifier(): PsiIdentifier = conidUsage

    override fun getMethods(): Array<PsiMethod> = constructs.constructList.toTypedArray()

    override fun isInterface(): Boolean = false

    override fun getScope(): PsiElement = this

    override val typedVaridDeclarations get(): List<FregeTypedVarid> = typedVaridList

    override fun generateDoc(): String = generateDoc("Data", "Constructors")
}
