package com.plugin.frege.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.impl.light.*
import com.intellij.psi.impl.source.HierarchicalMethodSignatureImpl
import com.intellij.psi.javadoc.PsiDocComment
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.MethodSignature
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod
import com.plugin.frege.FregeLanguage
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.FregePsiMethod
import com.plugin.frege.resolve.FregeResolveUtil
import com.plugin.frege.stubs.FregeMethodStub
import org.jetbrains.annotations.NonNls

@Suppress("UnstableApiUsage")
abstract class FregePsiMethodImpl : FregeNamedStubBasedPsiElementBase<FregeMethodStub>, FregePsiMethod {
    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeMethodStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    private val objectType: PsiType by lazy {
        PsiMethodReferenceType.getJavaLangObject(manager, GlobalSearchScope.everythingScope(project))
    }

    protected val objectTypeElement: LightTypeElement? by lazy {
        LightTypeElement(manager, objectType)
    }

    override fun onlyQualifiedSearch(): Boolean = false

    override fun getThrowsList(): PsiReferenceList =
        LightReferenceListBuilder(manager, FregeLanguage.INSTANCE, PsiReferenceList.Role.THROWS_LIST)

    override fun isVarArgs(): Boolean = false

    override fun findSuperMethods(): Array<PsiMethod> = PsiMethod.EMPTY_ARRAY // TODO

    override fun findSuperMethods(checkAccess: Boolean): Array<PsiMethod> = PsiMethod.EMPTY_ARRAY // TODO

    override fun findSuperMethods(parentClass: PsiClass): Array<PsiMethod> = PsiMethod.EMPTY_ARRAY // TODO

    override fun findSuperMethodSignaturesIncludingStatic(
        checkAccess: Boolean
    ): List<MethodSignatureBackedByPsiMethod> = emptyList() // TODO

    override fun findDeepestSuperMethod(): PsiMethod? = null // TODO

    override fun findDeepestSuperMethods(): Array<PsiMethod> = PsiMethod.EMPTY_ARRAY // TODO

    override fun isDeprecated(): Boolean = false // TODO

    override fun getDocComment(): PsiDocComment? = null // TODO

    override fun getContainingClass(): FregePsiClass? = FregeResolveUtil.findContainingFregeClass(this)

    override fun hasTypeParameters(): Boolean = false // Unless we want to support generics

    override fun getTypeParameterList(): PsiTypeParameterList? = null // Unless we want to support generics

    override fun getTypeParameters(): Array<PsiTypeParameter> =
        PsiTypeParameter.EMPTY_ARRAY // Unless we want to support generics

    protected abstract fun getParamsNumber(): Int

    override fun getParameterList(): PsiParameterList {
        val list = LightParameterListBuilder(manager, FregeLanguage.INSTANCE)
        val paramsNumber = getParamsNumber()
        for (i in 0 until paramsNumber) {
            list.addParameter(LightParameter("arg$i", objectType, this))
        }
        return list // TODO type inference
    }

    override fun getSignature(substitutor: PsiSubstitutor): MethodSignature =
        MethodSignatureBackedByPsiMethod.create(this, substitutor)

    override fun getModifierList(): LightModifierList {
        val baseList = LightModifierList(manager, FregeLanguage.INSTANCE, PsiModifier.FINAL)
        baseList.addModifier(accessPsiModifier)
        return baseList
    }

    override fun hasModifierProperty(@NonNls name: String): Boolean = modifierList.hasModifierProperty(name)

    override fun getName(): String = greenStub?.name ?: nameIdentifier?.text ?: text

    override fun getHierarchicalMethodSignature(): HierarchicalMethodSignature {
        return HierarchicalMethodSignatureImpl(
            MethodSignatureBackedByPsiMethod.create(this, PsiSubstitutor.EMPTY)
        )
    }

    override fun getReturnType(): PsiType = objectType // TODO waiting for type system

    override fun getReturnTypeElement(): PsiTypeElement? = objectTypeElement // TODO waiting for type system
}
