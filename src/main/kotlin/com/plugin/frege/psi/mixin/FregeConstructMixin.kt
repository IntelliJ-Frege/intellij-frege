package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.impl.light.LightModifierList
import com.intellij.psi.impl.source.tree.java.PsiCodeBlockImpl
import com.intellij.psi.stubs.IStubElementType
import com.plugin.frege.FregeLanguage
import com.plugin.frege.psi.FregeConstruct
import com.plugin.frege.psi.impl.FregePsiMethodImpl
import com.plugin.frege.stubs.FregeMethodStub

abstract class FregeConstructMixin: FregePsiMethodImpl, FregeConstruct {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeMethodStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getParamsNumber(): Int {
        return if (sigmaList.isNotEmpty()) {
            sigmaList.size
        } else {
            labelsList.size
        }
    }

    override fun getNameIdentifier(): PsiIdentifier {
        return conidUsage
    }

    override fun getModifierList(): PsiModifierList {
        return LightModifierList(manager, FregeLanguage.INSTANCE, PsiModifier.PUBLIC, PsiModifier.FINAL)
    }

    override fun getBody(): PsiCodeBlock {
        return PsiCodeBlockImpl(text)
    }

    override fun isConstructor(): Boolean {
        return true
    }
}
