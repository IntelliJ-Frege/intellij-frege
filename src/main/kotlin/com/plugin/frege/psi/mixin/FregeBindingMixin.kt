package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.impl.light.LightModifierList
import com.intellij.psi.impl.source.tree.java.PsiCodeBlockImpl
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import com.plugin.frege.documentation.FregeDocUtil
import com.plugin.frege.documentation.buildDoc
import com.plugin.frege.psi.*
import com.plugin.frege.psi.impl.FregeAnnotationItemImpl
import com.plugin.frege.psi.impl.FregePsiMethodImpl
import com.plugin.frege.psi.util.FregePsiUtil
import com.plugin.frege.stubs.FregeMethodStub

@Suppress("UnstableApiUsage")
abstract class FregeBindingMixin : FregePsiMethodImpl, FregeWeakScopeElement, FregeBinding {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeMethodStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun onlyQualifiedSearch(): Boolean {
        val containingClass = containingClass
        return containingClass is FregeNewtypeDecl || containingClass is FregeInstanceDecl
    }

    override fun getSubprogramsFromScope(): List<PsiElement> {
        return rhs.whereSection?.linearIndentSection?.subprogramsFromScope ?: emptyList()
    }

    override fun getBody(): PsiCodeBlock {
        return PsiCodeBlockImpl(text)
    }

    override fun isConstructor(): Boolean {
        return false
    }

    override fun getNameIdentifier(): PsiIdentifier? {
        val functionLhs = lhs.functionLhs
        functionLhs?.functionName?.let { return it }
        functionLhs?.symbolOperatorQuoted?.symbolOperator?.let { return it }
        val lexOperator = functionLhs?.lexOperator
        lexOperator?.symbolOperatorQuoted?.symbolOperator?.let { return it }
        lexOperator?.wordOperator?.let { return it }
        return null
    }

    override fun getModifierList(): LightModifierList {
        val baseList = super.getModifierList()
        if (containingClass is FregeProgram) {
            baseList.addModifier(PsiModifier.STATIC)
        }
        return baseList
    }

    // TODO
    override fun getParamsNumber(): Int {
        val funLhs = lhs.functionLhs ?: return 0
        return PsiTreeUtil.findChildrenOfType(funLhs, FregeParameter::class.java).size
    }

    override fun generateDoc(): String {
        val annoItem = getAnnoItem() as? FregeAnnotationItemImpl
        val type = annoItem?.getAnnotation()?.sigma?.text
        return buildDoc {
            definition {
                appendModuleLink(parentOfType())
                appendNewline()
                appendText("Function ")
                appendBoldText(name)
                if (type != null) {
                    appendNewline()
                    appendText("Type: ")
                    appendCode(type)
                }
                if (containingClass != null && containingClass !is FregeProgram) {
                    appendNewline()
                    appendText("within ")
                    appendPsiClassLink(containingClass)
                }
            }
            content {
                if (annoItem != null) {
                    appendDocs(FregeDocUtil.collectDocComments(annoItem))
                    appendNewline()
                }
                appendDocs(FregeDocUtil.collectDocComments(this@FregeBindingMixin))
            }
        }
    }

    fun getAnnoItem(): FregeAnnotationItem? {
        val referenceText = name
        return FregePsiUtil.findElementsWithinScope(parent) { elem ->
            elem is FregeAnnotationItem && elem.name == referenceText
        }.firstOrNull() as? FregeAnnotationItem
    }

    fun isMainFunctionBinding(): Boolean {
        val argsCount = getParamsNumber()
        return (argsCount <= 1 && FregePsiUtil.isInGlobalScope(this) && name == "main")
    }
}
