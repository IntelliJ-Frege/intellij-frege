package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.parentOfTypes
import com.plugin.frege.psi.FregeDecl
import com.plugin.frege.psi.FregePackageName
import com.plugin.frege.psi.FregeProgram
import com.plugin.frege.psi.FregeTypes
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.psi.impl.FregePsiUtilImpl.subprogramsFromScopeOfElement
import com.plugin.frege.stubs.FregeClassStub

@Suppress("UnstableApiUsage")
open class FregePackageClassNameMixin : FregePsiClassImpl, PsiIdentifier {
    constructor(node: ASTNode) : super(node)
    constructor(stub: FregeClassStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameIdentifier(): PsiIdentifier {
        return this
    }

    override fun getQualifiedName(): @NlsSafe String {
        val packageName = parentOfTypes(FregePackageName::class)
        return if (packageName != null) packageName.text else text
    }

    override fun getName(): @NlsSafe String {
        return text
    }

    override fun isInterface(): Boolean {
        return false
    }

    override fun getMethods(): Array<PsiMethod> {
        val program = parentOfTypes(FregeProgram::class)
            ?: throw IllegalStateException("Package must have a program above.")

        val body = program.body ?: return PsiMethod.EMPTY_ARRAY

        return subprogramsFromScopeOfElement(body) { decl -> (decl as? FregeDecl)?.binding }
            .asSequence()
            .mapNotNull { binding -> binding.lhs.funLhs }
            .mapNotNull { lhs -> lhs.functionName }
            .toList().toTypedArray()
    }

    override fun setName(name: @NlsSafe String): PsiElement {
        return this // TODO
    }

    override fun getScope(): PsiElement {
        return parentOfTypes(FregeProgram::class)
            ?: throw IllegalStateException("Package must have a program above.")
    }

    override fun getTokenType(): IElementType {
        return FregeTypes.PACKAGE_CLASS_NAME
    }
}
