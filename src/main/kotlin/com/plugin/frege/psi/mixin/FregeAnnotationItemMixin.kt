package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.impl.light.LightModifierList
import com.intellij.psi.impl.source.tree.java.PsiCodeBlockImpl
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.parentOfType
import com.plugin.frege.FregeLanguage
import com.plugin.frege.psi.FregeAnnotation
import com.plugin.frege.psi.FregeAnnotationItem
import com.plugin.frege.psi.FregeDocumentationElement
import com.plugin.frege.psi.FregeSimpleType
import com.plugin.frege.psi.impl.FregePsiMethodImpl
import com.plugin.frege.psi.impl.FregePsiUtilImpl
import com.plugin.frege.stubs.FregeMethodStub

abstract class FregeAnnotationItemMixin : FregePsiMethodImpl, FregeAnnotationItem {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeMethodStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getParamsNumber(): Int {
        val annotation = parent as? FregeAnnotation
        return annotation?.sigma?.children?.count { it is FregeSimpleType } ?: 0 // TODO it's VERY BAD. Waiting for grammar update.
    }

    override fun getModifierList(): PsiModifierList {
        return LightModifierList(manager, FregeLanguage.INSTANCE, PsiModifier.PUBLIC) // TODO
    }

    override fun getNameIdentifier(): PsiIdentifier? {
        return annotationName ?: symbolOperatorQuoted?.symbolOperator
    }

    override fun getBody(): PsiCodeBlock? {
        return PsiCodeBlockImpl(text)
    }

    override fun isConstructor(): Boolean {
        return false
    }

    fun getAnnotation(): FregeAnnotation {
        return parentOfType()!!
    }

    override fun getDocs(): List<FregeDocumentationElement> {
        return listOfNotNull(parentOfType<FregeAnnotation>()?.documentation) +
                FregePsiUtilImpl.collectPrecedingDocs(this)
    }
}
