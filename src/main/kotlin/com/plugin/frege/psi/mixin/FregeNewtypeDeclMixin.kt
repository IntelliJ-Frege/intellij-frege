package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.stubs.IStubElementType
import com.intellij.util.containers.addIfNotNull
import com.plugin.frege.psi.*
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.stubs.FregeClassStub

abstract class FregeNewtypeDeclMixin : FregePsiClassImpl<FregeClassStub>, FregeNewtypeDecl {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeClassStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameWithoutStub(): String = nameIdentifier?.text ?: DEFAULT_CLASS_NAME

    override fun getNameIdentifier(): PsiIdentifier? = conidUsage

    override fun getMethods(): Array<PsiMethod> {
        val methods: MutableList<FregePsiMethod> = whereSection?.linearIndentSection?.subprogramsFromScope
            ?.mapNotNull { (it as? FregeDecl)?.binding }
            ?.toMutableList() ?: mutableListOf()
        methods.addIfNotNull(construct)
        return methods.toTypedArray()
    }

    override fun generateDoc(): String = generateDoc("Newtype", "Constructor")

    override fun isInterface(): Boolean = false

    override fun getScope(): PsiElement = this

    override val typedVaridDeclarations get(): List<FregeTypedVarid> = typedVaridList
}
