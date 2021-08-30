package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.psi.*
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.stubs.FregeClassStub

@Suppress("UnstableApiUsage")
abstract class FregeInstanceDeclMixin : FregePsiClassImpl<FregeClassStub>, FregeInstanceDecl {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeClassStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun canBeReferenced(): Boolean = false

    override fun getNameWithoutStub(): String = nameIdentifier?.text ?: DEFAULT_CLASS_NAME

    override fun getNameIdentifier(): PsiIdentifier? {
        val typeApplications = typeApplications?.typeApplicationList ?: return null
        if (typeApplications.size != 1) {
            return null
        }
        val application = typeApplications[0]
        return PsiTreeUtil.findChildOfType(application, FregeConidUsage::class.java)
    }

    override fun getMethods(): Array<PsiMethod> {
        return whereSection?.linearIndentSection?.subprogramsFromScope
            ?.mapNotNull { it.firstChild as? FregeBinding }
            ?.filter { it.nameIdentifier?.reference?.resolve() === it }
            ?.toTypedArray() ?: PsiMethod.EMPTY_ARRAY
    }

    override fun isInterface(): Boolean = false

    override fun getScope(): PsiElement = this

    override val typedVaridDeclarations: List<FregeTypedVarid>
        get() = PsiTreeUtil.findChildrenOfType(typeApplications, FregeTypedVarid::class.java).toList()

    fun getInstancedClass(): FregeClassDecl? = qConid?.conidUsage?.reference?.resolve() as? FregeClassDecl

    override fun generateDoc(): String = "" // TODO
}
