package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.*
import com.intellij.psi.impl.light.LightModifierList
import com.intellij.psi.impl.source.tree.java.PsiCodeBlockImpl
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.PsiTreeUtil
import com.plugin.frege.FregeLanguage
import com.plugin.frege.psi.FregeAnnotation
import com.plugin.frege.psi.FregeBinding
import com.plugin.frege.psi.FregeParam
import com.plugin.frege.psi.impl.FregePsiMethodImpl
import com.plugin.frege.psi.impl.FregePsiUtilImpl
import com.plugin.frege.stubs.FregeMethodStub

@Suppress("UnstableApiUsage")
abstract class FregeBindingMixin : FregePsiMethodImpl, FregeBinding {
    private val modifierList: LightModifierList

    constructor(node: ASTNode) : super(node) {
        modifierList = LightModifierList(manager, FregeLanguage.INSTANCE,
            PsiModifier.STATIC, PsiModifier.FINAL, PsiModifier.PUBLIC
        ) // TODO
    }

    constructor(stub: FregeMethodStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType) {
        modifierList = LightModifierList(manager, FregeLanguage.INSTANCE,
            PsiModifier.STATIC, PsiModifier.FINAL, PsiModifier.PUBLIC
        ) // TODO
    }

    override fun getBody(): PsiCodeBlock {
        return PsiCodeBlockImpl(text)
    }

    override fun isConstructor(): Boolean {
        return false
    }

    override fun getNameIdentifier(): PsiIdentifier? {
        return lhs.funLhs?.functionName
    }

    override fun getModifierList(): PsiModifierList {
        return modifierList
    }

    override fun setName(name: @NlsSafe String): PsiElement {
        return this // handleElementRename in FunctionName performs renaming
    }

    // TODO
    override fun getParamsNumber(): Int {
        val funLhs = lhs.funLhs ?: return 0
        return PsiTreeUtil.findChildrenOfType(funLhs, FregeParam::class.java).size
    }

    fun getAnnotation(): FregeAnnotation? {
        val referenceText = name
        return FregePsiUtilImpl.findElementsWithinScope(this) { elem ->
            elem is FregeAnnotation && elem.name == referenceText
        }.firstOrNull() as? FregeAnnotation
    }

    fun isMainFunctionBinding(): Boolean {
        val argsCount = getParamsNumber()
        return (argsCount <= 1 && FregePsiUtilImpl.isInGlobalScope(this) && name == "main")
    }
}
