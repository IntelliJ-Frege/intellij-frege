package com.plugin.frege.psi.util

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.parentOfTypes
import com.plugin.frege.psi.*
import com.plugin.frege.psi.impl.FregeNamedStubBasedPsiElementBase
import com.plugin.frege.psi.impl.FregePsiClassImpl
import com.plugin.frege.psi.mixin.FregeAccessModifiers
import com.plugin.frege.psi.mixin.FregeProgramUtil.imports
import kotlin.reflect.KClass

object FregePsiUtil {
    fun isScope(element: PsiElement?): Boolean = element is FregeScopeElement

    private fun isWeakScope(element: PsiElement?): Boolean = element is FregeWeakScopeElement

    /**
     * Finds the first parent of [element] that presents a scope.
     * @return the [element], if it is a scope or `null` if there is no a scope for [element].
     */
    @JvmStatic
    fun scopeOfElement(element: PsiElement): FregeScopeElement? = element.parentOfType(true)

    /**
     * The same as [scopeOfElement] but skips scopes which are [FregeWeakScopeElement].
     */
    @JvmStatic
    fun notWeakScopeOfElement(element: PsiElement): FregeScopeElement? {
        var current = element.parentOfType<FregeScopeElement>(true)
        while (current != null) {
            if (!isWeakScope(current)) {
                return current
            }
            current = current.parentOfType(false)
        }
        return null
    }

    /**
     * Finds a scope of [element], gets a list of [PsiElement]
     * and applies [getter] for each of [PsiElement].
     * Also filters `nulls` in the resulting list.
     */
    @JvmStatic
    fun <T : PsiElement> subprogramsFromScopeOfElement(element: PsiElement, getter: (elem: PsiElement) -> T?): List<T> {
        val scope = scopeOfElement(element)
        return if (isScope(scope)) {
            scope!!.subprogramsFromScope.mapNotNull { getter(it) }
        } else {
            emptyList()
        }
    }

    /**
     * @return a scope of [element] and gets a list of subprograms.
     */
    @JvmStatic
    fun subprogramsFromScopeOfElement(element: PsiElement): List<PsiElement> =
        subprogramsFromScopeOfElement(element) { it }

    /**
     * @return a predicate, accepting only instance of [clazz]
     * for which [PsiNamedElement.getName] equals [name] if [incompleteCode] is `false`
     */
    @JvmStatic
    fun getPredicateCheckingTypeAndName(
        clazz: KClass<out PsiNamedElement>, name: FregeName, incompleteCode: Boolean
    ): (elem: PsiElement?) -> Boolean {
        val instancePredicate = { elem: PsiElement? -> clazz.isInstance(elem) }
        return if (!incompleteCode) { elem ->
            instancePredicate(elem) && (elem as? PsiNamedElement)?.name == name.shortName
        } else {
            instancePredicate
        }
    }

    /**
     * Checks if [scope] is global for a file.
     * @throws IllegalArgumentException if [scope] is not a scope.
     */
    @JvmStatic
    fun isScopeGlobal(scope: PsiElement?): Boolean {
        require(isScope(scope)) { "The passed element is not a scope." }
        return scope is FregeBody
    }

    /**
     * Checks [element] is in the global scope.
     * It means that it is one of [FregeTopDecl].
     */
    @JvmStatic
    fun isInGlobalScope(element: PsiElement): Boolean = isScopeGlobal(notWeakScopeOfElement(element))

    /**
     * @return a global scope for [element] or `null` if there is no scope.
     */
    @JvmStatic
    fun globalScopeOfElement(element: PsiElement?): PsiElement? {
        return if (element == null) {
            null
        } else if (isScope(element) && isScopeGlobal(element)) {
            element
        } else {
            globalScopeOfElement(element.parent)
        }
    }

    /**
     * @return elements within the scope of [element] that match [predicate].
     */
    @JvmStatic
    fun findElementsWithinScope(element: PsiElement, predicate: (elem: PsiElement) -> Boolean): List<PsiElement> {
        val subprograms = subprogramsFromScopeOfElement(element)
        return subprograms.flatMap { findElementsWithinElementSequence(it, predicate) }
    }

    @JvmStatic
    private fun findElementsWithinElementSequence(
        element: PsiElement?,
        predicate: (elem: PsiElement) -> Boolean
    ): Sequence<PsiElement> {
        return when {
            element == null -> emptySequence()
            predicate(element) -> sequenceOf(element)
            isScope(element) && !isWeakScope(element) -> emptySequence()
            else -> element.children.asSequence().flatMap {
                findElementsWithinElementSequence(it, predicate)
            }
        }
    }

    /**
     * @return elements matched the [predicate] in the children of [element] within its scope.
     */
    @JvmStatic
    fun findElementsWithinElement(
        element: PsiElement?,
        predicate: (elem: PsiElement) -> Boolean
    ): List<PsiElement> = findElementsWithinElementSequence(element, predicate).toList()

    /**
     * @return the module name of [psi], if presented, or `null` otherwise
     */
    @JvmStatic
    fun getModuleName(psi: PsiElement): String? {
        val fregeProgram = psi.parentOfTypes(FregeProgram::class, withSelf = true)
        requireNotNull(fregeProgram) { "PsiElement is not a part of Frege program" }
        return fregeProgram.packageName?.text
    }

    /**
     * Checks if [element] is a leaf in the PSI tree.
     */
    @JvmStatic
    fun isLeaf(element: PsiElement): Boolean = element.firstChild == null

    /**
     * It is a workaround while we don't have a type system.
     * @return first [FregeConidUsage] from [sigma] if it's not the arrow type. Otherwise `null` will be returned.
     */
    @JvmStatic
    fun findMainTypeFromSigma(sigma: FregeSigma?): FregeConidUsage? {
        val rho = sigma?.rho ?: return null
        if (rho.rho != null) { // it's the arrow type
            return null
        }
        val simpleType = rho.typeApplication?.simpleTypeList?.firstOrNull() ?: return null
        return PsiTreeUtil.findChildOfType(simpleType, FregeConidUsage::class.java)
    }

    /**
     * @return the previous siblings of [element] without [skip].
     * If [strict] = `true` then [element] will be returned as well.
     */
    @JvmStatic
    fun siblingsBackwardSequenceSkipping(
        element: PsiElement,
        strict: Boolean,
        skip: TokenSet
    ): Sequence<PsiElement> {
        val start = if (strict) element.prevSibling else element
        return generateSequence(start) { it.prevSibling }.filter {
            !skip.contains(it.elementType)
        }
    }

    /**
     * @return is [element] type may separates declarations
     */
    @JvmStatic
    fun isEndDeclElement(element: PsiElement): Boolean {
        val type = element.elementType
        return FregeTypes.VIRTUAL_END_DECL.equals(type) || FregeTypes.SEMICOLON.equals(type)
    }

    /**
     * Checks if within children of [element] there is an PSI node of [type].
     */
    @JvmStatic
    fun isElementTypeWithinChildren(element: PsiElement, type: IElementType): Boolean =
        generateSequence(element.firstChild) { it.nextSibling }.any { it.elementType === type }

    /**
     * Required for navigation to modules from the standard library.
     * According to the language rules, '`frege`' can be omitted in the imported package,
     * provided if you replace the first letter in the next word with the upper-cased one.
     *
     * Example: 'frege.data.HashMap -> Data.HashMap'
     *
     * @return converted [packageName] to the package with the '`frege`' prefix
     * or `null` if it's not possible ([packageName] is empty or the first letter is not upper-cased)
     */
    @JvmStatic
    fun tryConvertToLibraryPackage(packageName: String): String? {
        if (packageName.isEmpty() || !packageName.first().isUpperCase()) {
            return null
        }
        return "frege.${packageName.first().lowercaseChar()}${packageName.substring(1)}"
    }

    /**
     * Checks if [element] can be accessed as a usage from [module].
     */
    @JvmStatic
    fun isElementAccessibleFromModule(element: FregeNamedStubBasedPsiElementBase<*>, module: FregeProgram): Boolean {
        val elementModule = element.parentOfType<FregeProgram>(withSelf = true) ?: return false
        if (elementModule === module) {
            return true
        }
        val elementModifier = element.accessModifiers
        val modifier = when (element) {
            is FregePsiClass -> elementModifier
            is FregePsiMethod -> {
                val clazz = element.containingClass as? FregePsiClassImpl<*>
                when {
                    clazz is FregeProgram -> elementModifier
                    clazz != null -> maxOf(elementModifier, clazz.accessModifiers)
                    else -> null
                }
            }
            else -> null
        }
        return when (modifier) {
            null -> false
            FregeAccessModifiers.Public -> true
            FregeAccessModifiers.Private -> false
            FregeAccessModifiers.Protected -> {
                val elementModuleName = elementModule.qualifiedName
                val elementName = element.name
                var accessible = false
                val visitor = object : PsiRecursiveElementVisitor() {
                    override fun visitElement(currentElement: PsiElement) {
                        if (!accessible) {
                            if (currentElement.text == elementName) {
                                accessible = true
                            } else {
                                super.visitElement(currentElement)
                            }
                        }
                    }
                }
                module.imports.asSequence()
                    .filter { it.importPackageName?.text == elementModuleName }
                    .onEach { it.accept(visitor) }
                    .takeWhile { !accessible } // optimization
                    .toList()

                accessible
            }
        }
    }

    /**
     * Before using this method, look at [FregeName]! This should be used ONLY with Java-names and stubs.
     * @return the word after the last '.' in [qualifiedName].
     */
    @JvmStatic
    fun nameFromQualifiedName(qualifiedName: String): String =
        if (qualifiedName == "." || qualifiedName.endsWith("..")) "." else qualifiedName.substringAfterLast(".")

    /**
     * Before using this method, look at [FregeName]! This should be used ONLY with Java-names and stubs.
     * @return the prefix before the last '.' in [qualifiedName].
     */
    @JvmStatic
    fun qualifierFromQualifiedName(qualifiedName: String): String =
        if (qualifiedName.endsWith("..")) qualifiedName.dropLast(2) else qualifiedName.substringBeforeLast(".", "")

    /**
     * Before using this method, look at [FregeName]! This should be used ONLY with Java-names and stubs.
     * @return if [name] does contain a qualifier.
     */
    @JvmStatic
    fun isNameQualified(name: String): Boolean = name.contains('.') && name != "."
}
