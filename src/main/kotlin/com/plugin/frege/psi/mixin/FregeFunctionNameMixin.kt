package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.*
import com.intellij.psi.impl.light.LightModifierList
import com.intellij.psi.impl.source.HierarchicalMethodSignatureImpl
import com.intellij.psi.impl.source.tree.java.PsiCodeBlockImpl
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfTypes
import com.plugin.frege.FregeLanguage
import com.plugin.frege.psi.*
import com.plugin.frege.psi.impl.FregePsiMethodImpl
import com.plugin.frege.psi.impl.FregePsiUtilImpl
import com.plugin.frege.psi.impl.FregePsiUtilImpl.isInGlobalScope
import com.plugin.frege.resolve.FregeFunctionNameReference
import com.plugin.frege.stubs.FregeMethodStub

@Suppress("UnstableApiUsage")
open class FregeFunctionNameMixin : FregePsiMethodImpl, PsiIdentifier {
    private val modifierList: LightModifierList

    constructor(node: ASTNode) : super(node) {
        modifierList = LightModifierList(manager, FregeLanguage.INSTANCE,
            PsiModifier.STATIC, PsiModifier.FINAL, PsiModifier.PUBLIC
        ) // TODO
    }

    constructor(stub: FregeMethodStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType) {
        modifierList = LightModifierList(manager, FregeLanguage.INSTANCE)
    }

    override fun getReturnType(): PsiType {
        return objectType!!
    }

    override fun getReturnTypeElement(): PsiTypeElement? {
        return objectTypeElement
    }

    override fun getBody(): PsiCodeBlock {
        val binding = PsiTreeUtil.getParentOfType(this, FregeBinding::class.java)
        val text = if (binding == null) text else binding.text // TODO
        return PsiCodeBlockImpl(text)
    }

    override fun isConstructor(): Boolean {
        return false
    }

    override fun getNameIdentifier(): PsiIdentifier {
        return this
    }

    override fun getModifierList(): PsiModifierList {
        return modifierList
    }

    override fun setName(name: @NlsSafe String): PsiElement {
        return nameIdentifier.replace(FregeElementFactory.createFunctionName(project, name))
    }

    override fun getHierarchicalMethodSignature(): HierarchicalMethodSignature {
        return HierarchicalMethodSignatureImpl(
            MethodSignatureBackedByPsiMethod.create(this, PsiSubstitutor.EMPTY)
        )
    }

    override fun getReference(): PsiReference? {
        return FregeFunctionNameReference(this)
    }

    // TODO
    override fun getParamsNumber(): Int {
        if (!isFunctionBinding()) {
            return 0 // TODO
        }
        val fregeLhs = parentOfTypes(FregeLhs::class)!!
        return PsiTreeUtil.findChildrenOfType(fregeLhs, FregeParam::class.java).size
    }

    fun getAnnotationName(): FregeAnnotationName? {
        val referenceText = text
        return FregePsiUtilImpl.findElementsWithinScope(this) { elem ->
            elem is FregeAnnotationName && elem.getText() == referenceText
        }.firstOrNull() as? FregeAnnotationName
    }

    fun isFunctionBinding(): Boolean = parent is FregeFunLhs

    fun isMainFunctionBinding(): Boolean {
        val argsCount = getParamsNumber()
        return (isFunctionBinding()
                && argsCount <= 1 && isInGlobalScope(this)
                && text == "main")
    }

    override fun getTokenType(): IElementType {
        return FregeTypes.FUNCTION_NAME
    }
}
