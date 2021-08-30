package com.plugin.frege.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Pair
import com.intellij.psi.*
import com.intellij.psi.impl.light.LightModifierList
import com.intellij.psi.impl.light.LightPsiClassBuilder
import com.intellij.psi.javadoc.PsiDocComment
import com.plugin.frege.FregeLanguage
import com.plugin.frege.psi.FregeDocumentableElement
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.resolve.FregeResolveUtil

/**
 * Serves to delegate to another class.
 *
 * If [delegate] returns `null`, then a fake delegate will be created.
 */
abstract class LightFregePsiClassBase(node: ASTNode) : FregeNamedElementImpl(node), FregePsiClass {
    /**
     * @return a class that should be used to replace calls of most methods.
     */
    protected abstract val delegate: PsiClass?

    private val delegateOrFake get(): PsiClass = delegate ?: fakeDelegate

    private val fakeDelegate get(): PsiClass = LightPsiClassBuilder(containingClass ?: this, name ?: DEFAULT_NAME)

    override fun setName(name: String): PsiElement = apply {
        nameIdentifier?.reference?.handleElementRename(name)
    }

    override fun getModifierList(): PsiModifierList = LightModifierList(manager, FregeLanguage.INSTANCE)

    override fun hasModifierProperty(name: String): Boolean = false

    override fun getContainingClass(): PsiClass? = FregeResolveUtil.findContainingFregeClass(this)

    override fun getDocComment(): PsiDocComment? = delegateOrFake.docComment

    override fun isDeprecated(): Boolean = delegateOrFake.isDeprecated

    override fun getTypeParameters(): Array<PsiTypeParameter> = delegateOrFake.typeParameters

    override fun hasTypeParameters(): Boolean = delegateOrFake.hasTypeParameters()

    override fun getTypeParameterList(): PsiTypeParameterList? = delegateOrFake.typeParameterList

    override fun getQualifiedName(): String? =
        (containingClass?.qualifiedName ?: DEFAULT_NAME) + (name ?: DEFAULT_NAME)

    override fun getMethods(): Array<PsiMethod> = delegateOrFake.methods

    override fun findMethodsByName(name: String?, checkBases: Boolean): Array<PsiMethod> =
        delegateOrFake.findMethodsByName(name, checkBases)

    override fun getFields(): Array<PsiField> = delegateOrFake.fields

    override fun getInnerClasses(): Array<PsiClass> = delegateOrFake.innerClasses

    override fun isInterface(): Boolean = delegateOrFake.isInterface

    override fun isAnnotationType(): Boolean = delegateOrFake.isAnnotationType

    override fun isEnum(): Boolean = delegateOrFake.isEnum

    override fun getExtendsList(): PsiReferenceList? = delegateOrFake.extendsList

    override fun getImplementsList(): PsiReferenceList? = delegateOrFake.implementsList

    override fun getExtendsListTypes(): Array<PsiClassType> = delegateOrFake.extendsListTypes

    override fun getImplementsListTypes(): Array<PsiClassType> = delegateOrFake.implementsListTypes

    override fun getSuperClass(): PsiClass? = delegateOrFake.superClass

    override fun getInterfaces(): Array<PsiClass> = delegateOrFake.interfaces

    override fun getSupers(): Array<PsiClass> = delegateOrFake.supers

    override fun getSuperTypes(): Array<PsiClassType> = delegateOrFake.superTypes

    override fun getConstructors(): Array<PsiMethod> = delegateOrFake.constructors

    override fun getInitializers(): Array<PsiClassInitializer> = delegateOrFake.initializers

    override fun getAllFields(): Array<PsiField> = delegateOrFake.allFields

    override fun getAllMethods(): Array<PsiMethod> = delegateOrFake.allMethods

    override fun getAllInnerClasses(): Array<PsiClass> = delegateOrFake.allInnerClasses

    override fun findFieldByName(name: String?, checkBases: Boolean): PsiField? =
        delegateOrFake.findFieldByName(name, checkBases)

    override fun findMethodBySignature(patternMethod: PsiMethod?, checkBases: Boolean): PsiMethod? =
        delegateOrFake.findMethodBySignature(patternMethod, checkBases)

    override fun findMethodsBySignature(patternMethod: PsiMethod?, checkBases: Boolean): Array<PsiMethod> =
        delegateOrFake.findMethodsBySignature(patternMethod, checkBases)

    override fun findMethodsAndTheirSubstitutorsByName(
        name: String?,
        checkBases: Boolean
    ): List<Pair<PsiMethod, PsiSubstitutor>> = delegateOrFake.findMethodsAndTheirSubstitutorsByName(name, checkBases)

    override fun getAllMethodsAndTheirSubstitutors(): MutableList<Pair<PsiMethod, PsiSubstitutor>> =
        delegateOrFake.allMethodsAndTheirSubstitutors

    override fun findInnerClassByName(name: String?, checkBases: Boolean): PsiClass? =
        delegateOrFake.findInnerClassByName(name, checkBases)

    override fun getLBrace(): PsiElement? = this

    override fun getRBrace(): PsiElement? = this

    override fun getScope(): PsiElement = this

    override fun isInheritor(baseClass: PsiClass, checkDeep: Boolean): Boolean =
        delegateOrFake.isInheritor(baseClass, checkDeep)

    override fun isInheritorDeep(baseClass: PsiClass?, classToByPass: PsiClass?): Boolean =
        delegateOrFake.isInheritorDeep(baseClass, classToByPass)

    override fun getVisibleSignatures(): MutableCollection<HierarchicalMethodSignature> =
        delegateOrFake.visibleSignatures

    override fun generateDoc(): String =
        (delegateOrFake as? FregeDocumentableElement)?.generateDoc() ?: ""

    private companion object {
        private const val DEFAULT_NAME = ""
    }
}
