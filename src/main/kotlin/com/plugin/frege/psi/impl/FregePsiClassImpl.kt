package com.plugin.frege.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Pair
import com.intellij.psi.*
import com.intellij.psi.impl.light.LightModifierList
import com.intellij.psi.javadoc.PsiDocComment
import com.intellij.psi.scope.NameHint
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import com.plugin.frege.FregeLanguage
import com.plugin.frege.documentation.FregeDocUtil
import com.plugin.frege.documentation.buildDoc
import com.plugin.frege.psi.FregePsiClass
import com.plugin.frege.psi.FregePsiMethod
import com.plugin.frege.psi.util.FregePsiUtil
import com.plugin.frege.resolve.FregeResolveUtil
import com.plugin.frege.stubs.FregeClassStub
import org.jetbrains.annotations.NonNls

@Suppress("UnstableApiUsage")
abstract class FregePsiClassImpl<StubT : FregeClassStub> : FregeNamedStubBasedPsiElementBase<StubT>, FregePsiClass {
    constructor(node: ASTNode) : super(node)

    constructor(stub: StubT, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    protected abstract fun getNameWithoutStub(): String

    override fun getName(): String {
        val nameFromStub = greenStub?.name
        return if (nameFromStub != null) {
            FregePsiUtil.nameFromQualifiedName(nameFromStub)
        } else {
            getNameWithoutStub()
        }
    }

    override fun getQualifiedName(): String? {
        greenStub?.let { return it.name }
        return containingClass?.let { containingClass ->
            containingClass.qualifiedName?.let { parentQualifiedName ->
                "$parentQualifiedName.$name"
            }
        }
    }

    override fun isAnnotationType(): Boolean = false

    override fun isEnum(): Boolean = false

    override fun getExtendsList(): PsiReferenceList? = null // TODO

    override fun getImplementsList(): PsiReferenceList? = null // TODO

    override fun getExtendsListTypes(): Array<PsiClassType> = PsiClassType.EMPTY_ARRAY // TODO

    override fun getImplementsListTypes(): Array<PsiClassType> = PsiClassType.EMPTY_ARRAY // TODO

    override fun getSuperClass(): PsiClass? = null // TODO (or always null?)

    override fun getInterfaces(): Array<PsiClass> = PsiClass.EMPTY_ARRAY // TODO

    override fun getSupers(): Array<PsiClass> = PsiClass.EMPTY_ARRAY // TODO

    override fun getSuperTypes(): Array<PsiClassType> = PsiClassType.EMPTY_ARRAY // TODO

    override fun getFields(): Array<PsiField> = PsiField.EMPTY_ARRAY // TODO (figure out when functions become fields)

    override fun getConstructors(): Array<PsiMethod> = PsiMethod.EMPTY_ARRAY // TODO

    override fun getInnerClasses(): Array<PsiClass> = PsiClass.EMPTY_ARRAY // TODO (or always null?)

    override fun getInitializers(): Array<PsiClassInitializer> = PsiClassInitializer.EMPTY_ARRAY // TODO (or always null)

    override fun getAllFields(): Array<PsiField> = PsiField.EMPTY_ARRAY // TODO

    override fun getAllMethods(): Array<PsiMethod> = methods // TODO

    override fun getAllInnerClasses(): Array<PsiClass> = PsiClass.EMPTY_ARRAY // TODO

    override fun findFieldByName(@NonNls name: String, checkBases: Boolean): PsiField? = null // TODO

    override fun findMethodBySignature(patternMethod: PsiMethod, checkBases: Boolean): PsiMethod? {
        val methods = findMethodsBySignature(patternMethod, checkBases)
        return methods.firstOrNull()
    }

    override fun findMethodsBySignature(patternMethod: PsiMethod, checkBases: Boolean): Array<PsiMethod> {
        return findMethodsByName(patternMethod.name, checkBases).filter { method ->
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

    override fun getAllMethodsAndTheirSubstitutors(): List<Pair<PsiMethod, PsiSubstitutor>> =
        allMethods.map { Pair(it, EmptySubstitutor.EMPTY) }

    override fun findInnerClassByName(@NonNls name: String, checkBases: Boolean): PsiClass? = null // TODO

    override fun isInheritor(baseClass: PsiClass, checkDeep: Boolean): Boolean = false // TODO

    override fun isInheritorDeep(baseClass: PsiClass, classToByPass: PsiClass?): Boolean = false // TODO

    override fun getVisibleSignatures(): Collection<HierarchicalMethodSignature> =
        allMethods.map { it.hierarchicalMethodSignature }

    override fun isDeprecated(): Boolean = false // TODO

    override fun hasTypeParameters(): Boolean = false // Unless we want to support generics

    override fun getTypeParameterList(): PsiTypeParameterList? = null // Unless we want to support generics

    override fun getTypeParameters(): Array<PsiTypeParameter> =
        PsiTypeParameter.EMPTY_ARRAY // Unless we want to support generics

    override fun getLBrace(): PsiElement? = scope

    override fun getRBrace(): PsiElement? = scope.lastChild

    override fun getDocComment(): PsiDocComment? = null // TODO

    override fun getModifierList(): PsiModifierList {
        val baseList = LightModifierList(manager, FregeLanguage.INSTANCE, PsiModifier.FINAL)
        baseList.addModifier(accessPsiModifier)
        return baseList
    }

    override fun hasModifierProperty(@NonNls name: String): Boolean = modifierList.hasModifierProperty(name) // TODO

    override fun getContainingClass(): PsiClass? = FregeResolveUtil.findContainingFregeClass(this)

    override fun processDeclarations(
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement?,
        place: PsiElement
    ): Boolean {
        val nameHint = processor.getHint(NameHint.KEY)
        val name = nameHint?.getName(state)
        processMethods(processor, state, name)
        processClasses(processor, state, name)
        return false // TODO fields
    }

    private fun processMethods(
        processor: PsiScopeProcessor,
        state: ResolveState,
        nameHint: String?
    ) {
        val methods = if (nameHint == null) allMethods else findMethodsByName(nameHint, true)
        for (method in methods) {
            processor.execute(method, state)
        }
    }

    private fun processClasses(
        processor: PsiScopeProcessor,
        state: ResolveState,
        nameHint: String?
    ) {
        val allClasses = PsiTreeUtil.findChildrenOfType(scope, FregePsiClass::class.java).filter { it != this }
        val classes = if (nameHint == null) allClasses else allClasses.filter { it.name == nameHint }
        for (clazz in classes) {
            processor.execute(clazz, state)
        }
    }

    protected fun generateDoc(psiClassTitle: String, psiMethodsTitle: String): String {
        val uniqueMethods = allMethods.distinctBy { it.name }.mapNotNull { it as? FregePsiMethod }
        return buildDoc {
            definition {
                appendModuleLink(parentOfType())
                appendNewline()
                appendText("$psiClassTitle ")
                appendBoldText(name)
            }
            content {
                appendDocs(FregeDocUtil.collectDocComments(this@FregePsiClassImpl))
                section("$psiMethodsTitle:") {
                    for (method in uniqueMethods) {
                        paragraph { appendPsiMethodLink(method) }
                    }
                }
            }
        }
    }

    protected companion object {
        const val DEFAULT_CLASS_NAME: String = ""
    }
}
