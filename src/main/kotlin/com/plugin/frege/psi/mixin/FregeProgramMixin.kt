package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.stubs.IStubElementType
import com.plugin.frege.psi.FregeDecl
import com.plugin.frege.psi.FregeProgram
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.psi.impl.FregePsiUtilImpl
import com.plugin.frege.stubs.FregeClassStub

@Suppress("UnstableApiUsage")
abstract class FregeProgramMixin : FregePsiClassImpl, FregeProgram {
    private companion object {
        private const val DEFAULT_MODULE_NAME: String = "Main"
    }

    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeClassStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameIdentifier(): PsiIdentifier? {
        return packageName?.qConIdList?.lastOrNull()?.dataNameUsage
    }

    override fun getQualifiedName(): @NlsSafe String? {
        return packageName?.text ?: DEFAULT_MODULE_NAME
    }

    override fun getName(): @NlsSafe String {
        return nameIdentifier?.text ?: DEFAULT_MODULE_NAME
    }

    override fun isInterface(): Boolean {
        return false
    }

    override fun getMethods(): Array<PsiMethod> {
        val body = body ?: return PsiMethod.EMPTY_ARRAY
        return FregePsiUtilImpl.subprogramsFromScopeOfElement(body) { (it as? FregeDecl)?.binding }
            .asSequence()
            .sortedBy { it.textOffset }
            .distinctBy { it.name } // pattern-matching
            .toList().toTypedArray()
    }

    override fun setName(name: @NlsSafe String): PsiElement {
        return this // DataNameUsage performs renaming
    }

    override fun getScope(): PsiElement {
        return this
    }
}
