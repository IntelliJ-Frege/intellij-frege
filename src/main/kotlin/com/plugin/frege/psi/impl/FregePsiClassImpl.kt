package com.plugin.frege.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Pair
import com.intellij.psi.*
import com.intellij.psi.impl.light.LightModifierList
import com.intellij.psi.javadoc.PsiDocComment
import com.intellij.psi.scope.NameHint
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.stubs.IStubElementType
import com.plugin.frege.FregeLanguage
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.stubs.FregeClassStub
import org.jetbrains.annotations.NonNls

@Suppress("UnstableApiUsage")
abstract class FregePsiClassImpl : FregeNamedStubBasedPsiElementBase<FregeClassStub?>, FregePsiClass {
    private val modifierList: LightModifierList

    constructor(node: ASTNode) : super(node) {
        modifierList = LightModifierList(manager, FregeLanguage.INSTANCE, PsiModifier.PUBLIC, PsiModifier.FINAL) // TODO
    }

    constructor(stub: FregeClassStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType) {
        modifierList = LightModifierList(manager, FregeLanguage.INSTANCE)
    }

    override fun getName(): String {
        val qualifiedName = qualifiedName
        return if (qualifiedName != null)
            FregePsiUtilImpl.nameFromQualifiedName(qualifiedName)
        else
            text
    }

    override fun isAnnotationType(): Boolean {
        return false
    }

    override fun isEnum(): Boolean {
        return false
    }

    override fun getExtendsList(): PsiReferenceList? {
        return null // TODO
    }

    override fun getImplementsList(): PsiReferenceList? {
        return null // TODO
    }

    override fun getExtendsListTypes(): Array<PsiClassType> {
        return PsiClassType.EMPTY_ARRAY // TODO
    }

    override fun getImplementsListTypes(): Array<PsiClassType> {
        return PsiClassType.EMPTY_ARRAY // TODO
    }

    override fun getSuperClass(): PsiClass? {
        return null // TODO (or always null?)
    }

    override fun getInterfaces(): Array<PsiClass> {
        return PsiClass.EMPTY_ARRAY // TODO
    }

    override fun getSupers(): Array<PsiClass> {
        return PsiClass.EMPTY_ARRAY // TODO
    }

    override fun getSuperTypes(): Array<PsiClassType> {
        return PsiClassType.EMPTY_ARRAY // TODO
    }

    override fun getFields(): Array<PsiField> {
        return PsiField.EMPTY_ARRAY // TODO (figure out when functions become fields)
    }

    override fun getConstructors(): Array<PsiMethod> {
        return PsiMethod.EMPTY_ARRAY // TODO
    }

    override fun getInnerClasses(): Array<PsiClass> {
        return PsiClass.EMPTY_ARRAY // TODO (or always null?)
    }

    override fun getInitializers(): Array<PsiClassInitializer> {
        return PsiClassInitializer.EMPTY_ARRAY // TODO (or always null)
    }

    override fun getAllFields(): Array<PsiField> {
        return PsiField.EMPTY_ARRAY // TODO
    }

    override fun getAllMethods(): Array<PsiMethod> {
        return methods // TODO
    }

    override fun getAllInnerClasses(): Array<PsiClass> {
        return PsiClass.EMPTY_ARRAY // TODO
    }

    override fun findFieldByName(@NonNls name: String, checkBases: Boolean): PsiField? {
        return null // TODO
    }

    override fun findMethodBySignature(patternMethod: PsiMethod, checkBases: Boolean): PsiMethod? {
        val methods = findMethodsBySignature(patternMethod, checkBases)
        return methods.firstOrNull()
    }

    override fun findMethodsBySignature(patternMethod: PsiMethod, checkBases: Boolean): Array<PsiMethod> {
        val methodsByName = findMethodsByName(patternMethod.name, checkBases)
        return methodsByName.filter { method ->
            patternMethod.getSignature(EmptySubstitutor.getInstance()).parameterTypes.contentEquals(
                method.getSignature(EmptySubstitutor.getInstance()).parameterTypes
            )
        }.toTypedArray()
    }

    override fun findMethodsByName(@NonNls name: String, checkBases: Boolean): Array<PsiMethod> {
        val allMethods = if (checkBases) allMethods else methods
        return allMethods.filter { it.name == name }.toTypedArray()
    }

    override fun findMethodsAndTheirSubstitutorsByName(
        @NonNls name: String,
        checkBases: Boolean
    ): List<Pair<PsiMethod, PsiSubstitutor>> {
        return findMethodsByName(name, checkBases).map { Pair(it, EmptySubstitutor.EMPTY) }
    }

    override fun getAllMethodsAndTheirSubstitutors(): List<Pair<PsiMethod, PsiSubstitutor>> {
        return allMethods.map { Pair(it, EmptySubstitutor.EMPTY) }
    }

    override fun findInnerClassByName(@NonNls name: String, checkBases: Boolean): PsiClass? {
        return null // TODO
    }

    override fun isInheritor(baseClass: PsiClass, checkDeep: Boolean): Boolean {
        return false // TODO
    }

    override fun isInheritorDeep(baseClass: PsiClass, classToByPass: PsiClass?): Boolean {
        return false // TODO
    }

    override fun getVisibleSignatures(): Collection<HierarchicalMethodSignature> {
        return allMethods.map { it.hierarchicalMethodSignature }
    }

    override fun isDeprecated(): Boolean {
        return false // TODO
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

    override fun getLBrace(): PsiElement? {
        return scope
    }

    override fun getRBrace(): PsiElement? {
        return scope.lastChild
    }

    override fun getDocComment(): PsiDocComment? {
        return null // TODO
    }

    override fun getModifierList(): PsiModifierList {
        return modifierList
    }

    override fun hasModifierProperty(@NonNls name: String): Boolean {
        return getModifierList().hasModifierProperty(name) // TODO
    }

    override fun processDeclarations(
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement?,
        place: PsiElement
    ): Boolean {
        val nameHint = processor.getHint(NameHint.KEY)
        val name = nameHint?.getName(state)
        val methods = if (name == null) allMethods else findMethodsByName(name, true)
        for (method in methods) {
            processor.execute(method, state)
        }
        return false // TODO fields
    }

    override fun getContainingClass(): PsiClass? {
        return FregePsiClassUtilImpl.findContainingFregeClass(this)
    }
}
