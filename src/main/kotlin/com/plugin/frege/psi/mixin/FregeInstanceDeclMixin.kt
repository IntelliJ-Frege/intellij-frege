package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.psi.FregeBinding
import com.plugin.frege.psi.FregeClassDecl
import com.plugin.frege.psi.FregeConidUsage
import com.plugin.frege.psi.FregeInstanceDecl
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.stubs.FregeClassStub

@Suppress("UnstableApiUsage")
abstract class FregeInstanceDeclMixin : FregePsiClassImpl, FregeInstanceDecl {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeClassStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameWithoutStub(): @NlsSafe String {
        return nameIdentifier?.text ?: DEFAULT_CLASS_NAME
    }

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
            ?.toTypedArray() ?: PsiMethod.EMPTY_ARRAY
    }

    override fun isInterface(): Boolean {
        return false
    }

    override fun getScope(): PsiElement {
        return this
    }

    fun getInstancedClass(): FregeClassDecl? {
        return conidUsage?.reference?.resolve() as? FregeClassDecl
    }
}
