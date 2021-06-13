package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.stubs.IStubElementType
import com.plugin.frege.psi.FregeNativeDataDecl
import com.plugin.frege.psi.FregeNativeFunction
import com.plugin.frege.psi.FregeTypedVarid
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.stubs.FregeClassStub

@Suppress("UnstableApiUsage")
abstract class FregeNativeDataDeclMixin : FregePsiClassImpl<FregeClassStub>, FregeNativeDataDecl {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeClassStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameWithoutStub(): @NlsSafe String {
        return conidUsage.text
    }

    override fun isInterface(): Boolean {
        return false
    }

    override fun getMethods(): Array<PsiMethod> {
        return whereSection?.linearIndentSection?.subprogramsFromScope
            ?.mapNotNull { it.firstChild as? FregeNativeFunction }
            ?.toTypedArray() ?: PsiMethod.EMPTY_ARRAY
    }

    override fun getNameIdentifier(): PsiIdentifier {
        return conidUsage
    }

    override fun getScope(): PsiElement {
        return this
    }

    override val typedVaridDeclarations: List<FregeTypedVarid>
        get() = typedVaridList

    override fun generateDoc(): String {
        return generateDoc("Native data", "Functions")
    }
}
