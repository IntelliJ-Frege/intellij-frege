package com.plugin.frege.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.impl.light.LightParameter
import com.intellij.psi.impl.light.LightParameterListBuilder
import com.intellij.psi.impl.light.LightReferenceListBuilder
import com.intellij.psi.impl.light.LightTypeElement
import com.intellij.psi.impl.source.HierarchicalMethodSignatureImpl
import com.intellij.psi.javadoc.PsiDocComment
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.MethodSignature
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod
import com.plugin.frege.FregeLanguage
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.FregePsiMethod
import com.plugin.frege.resolve.FregeResolveUtil.findContainingFregeClass
import com.plugin.frege.stubs.FregeMethodStub
import org.jetbrains.annotations.NonNls

@Suppress("UnstableApiUsage")
abstract class FregePsiMethodImpl : FregeNamedStubBasedPsiElementBase<FregeMethodStub>, FregePsiMethod {
    protected companion object {
        private var objectType: PsiType? = null
    }

    protected var objectTypeElement: LightTypeElement? = null
        get() {
            if (field == null) {
                if (objectType != null) {
                    field = LightTypeElement(manager, objectType)
                }
            }
            return field
        }
        private set

    protected val objectType: PsiType?
        get() {
            if (Companion.objectType == null) {
                Companion.objectType =
                    PsiMethodReferenceType.getJavaLangObject(manager, GlobalSearchScope.everythingScope(project))
            }
            return Companion.objectType
        }

    constructor(node: ASTNode) : super(node)

    constructor(stub: FregeMethodStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getThrowsList(): PsiReferenceList {
        return LightReferenceListBuilder(manager, FregeLanguage.INSTANCE, PsiReferenceList.Role.THROWS_LIST)
    }

    override fun isVarArgs(): Boolean {
        return false
    }

    override fun findSuperMethods(): Array<PsiMethod> {
        return PsiMethod.EMPTY_ARRAY // TODO
    }

    override fun findSuperMethods(checkAccess: Boolean): Array<PsiMethod> {
        return PsiMethod.EMPTY_ARRAY // TODO
    }

    override fun findSuperMethods(parentClass: PsiClass): Array<PsiMethod> {
        return PsiMethod.EMPTY_ARRAY // TODO
    }

    override fun findSuperMethodSignaturesIncludingStatic(checkAccess: Boolean): List<MethodSignatureBackedByPsiMethod> {
        return emptyList() // TODO
    }

    override fun findDeepestSuperMethod(): PsiMethod? {
        return null // TODO
    }

    override fun findDeepestSuperMethods(): Array<PsiMethod> {
        return PsiMethod.EMPTY_ARRAY // TODO
    }

    override fun isDeprecated(): Boolean {
        return false // TODO
    }

    override fun getDocComment(): PsiDocComment? {
        return null // TODO
    }

    override fun getContainingClass(): FregePsiClass? {
        return findContainingFregeClass(this)
    }

    override fun hasTypeParameters(): Boolean {
        return false // Unless we want to support generics
    }

    override fun getTypeParameterList(): PsiTypeParameterList? {
        return null // Unless we want to support generics
    }

    override fun getTypeParameters(): Array<PsiTypeParameter> {
        return PsiTypeParameter.EMPTY_ARRAY // Unless we want to support generics
    }

    override fun getParameterList(): PsiParameterList {
        val list = LightParameterListBuilder(manager, FregeLanguage.INSTANCE)
        val paramsNumber = getParamsNumber()
        for (i in 0 until paramsNumber) {
            list.addParameter(LightParameter("arg$i", objectType as PsiType, this))
        }
        return list // TODO a normal type system
    }

    override fun getSignature(substitutor: PsiSubstitutor): MethodSignature {
        return MethodSignatureBackedByPsiMethod.create(this, substitutor)
    }

    override fun hasModifierProperty(@NonNls name: String): Boolean {
        return modifierList.hasModifierProperty(name)
    }

    override fun getName(): String {
        return greenStub?.name ?: nameIdentifier?.text ?: text
    }

    override fun getHierarchicalMethodSignature(): HierarchicalMethodSignature {
        return HierarchicalMethodSignatureImpl(
            MethodSignatureBackedByPsiMethod.create(this, PsiSubstitutor.EMPTY)
        )
    }

    override fun getReturnType(): PsiType {
        return objectType!! // TODO waiting for type system
    }

    override fun getReturnTypeElement(): PsiTypeElement? {
        return objectTypeElement // TODO waiting for type system
    }

    protected abstract fun getParamsNumber(): Int
}
