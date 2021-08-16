package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiCodeBlock
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.impl.source.tree.java.PsiCodeBlockImpl
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.parentOfType
import com.plugin.frege.documentation.FregeDocUtil
import com.plugin.frege.documentation.buildDoc
import com.plugin.frege.psi.*
import com.plugin.frege.psi.impl.FregePsiMethodImpl
import com.plugin.frege.psi.util.FregePsiUtil
import com.plugin.frege.stubs.FregeMethodStub

abstract class FregeAnnotationItemMixin : FregePsiMethodImpl, FregeAnnotationItem {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeMethodStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getParamsNumber(): Int {
        val annotation = parent as? FregeAnnotation
        return annotation?.sigma?.children?.count { it is FregeSimpleType } ?: 0 // TODO it's VERY BAD. Waiting for grammar update.
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

    fun getBindings(): List<FregeBinding> {
        val referenceText = name
        return FregePsiUtil.findElementsWithinScope(parent) { elem ->
            elem is FregeBinding && elem.name == referenceText
        }.mapNotNull { elem -> elem as? FregeBinding }.toList()
    }

    override fun generateDoc(): String {
        val sigma = getAnnotation().sigma
        return buildDoc {
            definition {
                appendModuleLink(parentOfType())
                appendNewline()
                appendText("Function ")
                appendBoldText(name)
                if (sigma != null) {
                    appendNewline()
                    appendText("Type: ")
                    appendCode(sigma.text)
                }
                if (containingClass != null && containingClass !is FregeProgram) {
                    appendNewline()
                    appendText("within ")
                    appendPsiClassLink(containingClass)
                }
            }
            content {
                appendDocs(FregeDocUtil.collectDocComments(this@FregeAnnotationItemMixin))
                section("Implementations:") {
                    for (binding in getBindings()) {
                        paragraph {
                            appendDocs(FregeDocUtil.collectDocComments(binding))
                            appendNewline()
                            appendCode(binding.lhs.text)
                        }
                    }
                }
            }
        }
    }
}
