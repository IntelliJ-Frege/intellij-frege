package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.impl.light.LightModifierList
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.FregeLanguage
import com.plugin.frege.psi.FregeClassDecl
import com.plugin.frege.psi.FregeDecl
import com.plugin.frege.psi.FregeTypedVarid
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.stubs.FregeClassStub

@Suppress("UnstableApiUsage")
abstract class FregeClassDeclMixin : FregePsiClassImpl<FregeClassStub>, FregeClassDecl {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeClassStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameWithoutStub(): String = nameIdentifier?.text ?: DEFAULT_CLASS_NAME

    override fun getNameIdentifier(): PsiIdentifier? = // TODO fix it after fix grammar of this rule
        conidUsage ?: constraints?.constraintList?.firstOrNull()?.qConid?.conidUsage

    override fun getMethods(): Array<PsiMethod> {
        val subprograms = whereSection?.linearIndentSection?.subprogramsFromScope ?: return PsiMethod.EMPTY_ARRAY
        return subprograms
            .mapNotNull { (it as? FregeDecl)?.annotation }
            .flatMap { it.annotationItemList }
            .toTypedArray()
    }

    override fun isInterface(): Boolean = true

    override fun getScope(): PsiElement = this

    override fun getModifierList(): PsiModifierList =
        LightModifierList(manager, FregeLanguage.INSTANCE, PsiModifier.PUBLIC, PsiModifier.ABSTRACT) // TODO

    override val typedVaridDeclarations: List<FregeTypedVarid>
        get() {
            return if (conidUsage != null) {
                val typedVarid = typedVarid
                if (typedVarid != null) listOf(typedVarid) else emptyList()
            } else {
                PsiTreeUtil.findChildrenOfType(constraints, FregeTypedVarid::class.java).toList()
            }
        }

    override fun generateDoc(): String {
        return generateDoc("Class", "Types or defaults methods")
    }
}
