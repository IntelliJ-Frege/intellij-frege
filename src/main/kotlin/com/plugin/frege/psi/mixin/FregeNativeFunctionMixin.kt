package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.lang.jvm.JvmModifier
import com.intellij.lang.jvm.JvmParameter
import com.intellij.lang.jvm.types.JvmReferenceType
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.java.PsiCodeBlockImpl
import com.intellij.psi.javadoc.PsiDocComment
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.MethodSignature
import com.intellij.psi.util.parentOfType
import com.plugin.frege.documentation.FregeDocUtil
import com.plugin.frege.documentation.buildDoc
import com.plugin.frege.psi.FregeNativeDataDecl
import com.plugin.frege.psi.FregeNativeFunction
import com.plugin.frege.psi.FregeProgram
import com.plugin.frege.psi.impl.FregeNativeFunctionNameImpl
import com.plugin.frege.psi.impl.FregePsiMethodImpl
import com.plugin.frege.stubs.FregeMethodStub

@Suppress("UnstableApiUsage")
abstract class FregeNativeFunctionMixin : FregePsiMethodImpl, FregeNativeFunction {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeMethodStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    private val delegatedMember: PsiMethod? // TODO fields
            get() = (nameIdentifier as? FregeNativeFunctionNameImpl)?.getDelegatedMember() as? PsiMethod

    override fun onlyQualifiedSearch(): Boolean {
        return containingClass is FregeNativeDataDecl
    }

    override fun getNameIdentifier(): PsiIdentifier? {
        val annotationItem = nativeAnnotationItem
        val nativeFunctionName = annotationItem.nativeFunctionName
        if (nativeFunctionName != null) {
            return nativeFunctionName
        }
        val symbolOperator = annotationItem.symbolOperatorQuoted?.symbolOperator
        if (symbolOperator != null) {
            return symbolOperator
        }
        return null // TODO make unary operator identifier
    }

    override fun generateDoc(): String {
        return buildDoc {
            definition {
                appendModuleLink(parentOfType())
                appendNewline()
                appendText("Native function ")
                appendBoldText(name)
                if (sigmaList.isNotEmpty()) {
                    appendNewline()
                    appendText("Type: ")
                    appendCode(sigmaList.mapNotNull { it.text }.joinToString(" | "))
                }
                if (containingClass != null && containingClass !is FregeProgram) {
                    appendNewline()
                    appendText("within ")
                    appendPsiClassLink(containingClass)
                }
            }
            content {
                appendDocs(FregeDocUtil.collectDocComments(this@FregeNativeFunctionMixin))
            }
        }
    }

    // Following overrides are just delegates

    override fun getIdentifyingElement(): PsiElement? {
        return delegatedMember?.identifyingElement ?: super<FregePsiMethodImpl>.getIdentifyingElement()
    }

    override fun getThrowsList(): PsiReferenceList {
        return delegatedMember?.throwsList ?: super.getThrowsList()
    }

    override fun isVarArgs(): Boolean {
        return delegatedMember?.isVarArgs ?: super.isVarArgs()
    }

    override fun getThrowsTypes(): Array<JvmReferenceType> {
        return delegatedMember?.throwsTypes ?: super<FregePsiMethodImpl>.getThrowsTypes()
    }

    override fun isDeprecated(): Boolean {
        return delegatedMember?.isDeprecated ?: super.isDeprecated()
    }

    override fun getDocComment(): PsiDocComment? {
        return delegatedMember?.docComment ?: super.getDocComment()
    }

    override fun hasTypeParameters(): Boolean {
        return delegatedMember?.hasTypeParameters() ?: super.hasTypeParameters()
    }

    override fun getTypeParameterList(): PsiTypeParameterList? {
        return delegatedMember?.typeParameterList ?: super.getTypeParameterList()
    }

    override fun getTypeParameters(): Array<PsiTypeParameter> {
        return delegatedMember?.typeParameters ?: super.getTypeParameters()
    }

    override fun getParamsNumber(): Int {
        return delegatedMember?.parameterList?.parametersCount ?: 0
    }

    override fun getBody(): PsiCodeBlock? {
        return delegatedMember?.body ?: PsiCodeBlockImpl(text)
    }

    override fun isConstructor(): Boolean {
        return delegatedMember?.isConstructor ?: false
    }

    override fun getReturnType(): PsiType {
        return delegatedMember?.returnType ?: super.getReturnType()
    }

    override fun hasParameters(): Boolean {
        return delegatedMember?.hasParameters() ?: super<FregePsiMethodImpl>.hasParameters()
    }

    override fun getReturnTypeElement(): PsiTypeElement? {
        return delegatedMember?.returnTypeElement ?: super.getReturnTypeElement()
    }

    override fun getParameterList(): PsiParameterList {
        return delegatedMember?.parameterList ?: super.getParameterList()
    }

    override fun getSignature(substitutor: PsiSubstitutor): MethodSignature {
        return delegatedMember?.getSignature(substitutor) ?: super.getSignature(substitutor)
    }

    override fun hasModifierProperty(name: String): Boolean {
        return delegatedMember?.hasModifierProperty(name) ?: super.hasModifierProperty(name)
    }

    override fun getAnnotations(): Array<PsiAnnotation> {
        return delegatedMember?.annotations ?: super<FregePsiMethodImpl>.getAnnotations()
    }

    override fun getAnnotation(fqn: String): PsiAnnotation? {
        return delegatedMember?.getAnnotation(fqn) ?: super<FregePsiMethodImpl>.getAnnotation(fqn)
    }

    override fun hasAnnotation(fqn: String): Boolean {
        return delegatedMember?.hasAnnotation(fqn) ?: super<FregePsiMethodImpl>.hasAnnotation(fqn)
    }

    override fun hasModifier(modifier: JvmModifier): Boolean {
        return delegatedMember?.hasModifier(modifier) ?: super<FregePsiMethodImpl>.hasModifier(modifier)
    }

    override fun getSourceElement(): PsiElement? {
        return delegatedMember?.sourceElement ?: super<FregePsiMethodImpl>.getSourceElement()
    }

    override fun getHierarchicalMethodSignature(): HierarchicalMethodSignature {
        return delegatedMember?.hierarchicalMethodSignature ?: super.getHierarchicalMethodSignature()
    }

    override fun getParameters(): Array<JvmParameter> {
        return delegatedMember?.parameters ?: super<FregePsiMethodImpl>.getParameters()
    }
}
