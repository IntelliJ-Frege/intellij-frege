package com.plugin.frege.psi.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.parentOfTypes
import com.plugin.frege.psi.*
import kotlin.reflect.KClass

object FregePsiUtilImpl {
    fun isScope(element: PsiElement?): Boolean {
        return element is FregeScopeElement
    }

    private fun isWeakScope(element: PsiElement?): Boolean {
        return element is FregeWeakScopeElement
    }

    /**
     * Finds the first parent of [element] that presents a scope.
     * @return the [element], if it is a scope or `null` if there is no a scope for [element].
     */
    @JvmStatic
    fun scopeOfElement(element: PsiElement): FregeScopeElement? {
        return element.parentOfType(true)
    }

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
    fun subprogramsFromScopeOfElement(element: PsiElement): List<PsiElement> {
        return subprogramsFromScopeOfElement(element) { it }
    }

    /**
     * @return a predicate, accepting only instance of [clazz]
     * for which [PsiNamedElement.getName] equals [name] if [incompleteCode] is `false`
     */
    @JvmStatic
    fun getByTypePredicateCheckingName(
        clazz: KClass<out PsiNamedElement>, name: String, incompleteCode: Boolean
    ): (elem: PsiElement?) -> Boolean {
        val instancePredicate = { elem: PsiElement? -> clazz.isInstance(elem) }
        return if (!incompleteCode) { elem ->
            instancePredicate(elem) && (elem as? PsiNamedElement)?.name == name
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
    fun isInGlobalScope(element: PsiElement): Boolean {
        return isScopeGlobal(notWeakScopeOfElement(element))
    }

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
            element == null -> {
                emptySequence()
            }
            predicate(element) -> {
                sequenceOf(element)
            }
            isScope(element) && !isWeakScope(element) -> {
                emptySequence()
            }
            else -> {
                element.children.asSequence().flatMap {
                    findElementsWithinElementSequence(it, predicate)
                }
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
    ): List<PsiElement> {
        return findElementsWithinElementSequence(element, predicate).toList()
    }

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
    fun isLeaf(element: PsiElement): Boolean {
        return element.firstChild == null
    }

    /**
     * @return the last word after the last '.' in [qualifiedName].
     */
    @JvmStatic
    fun nameFromQualifiedName(qualifiedName: String): String {
        return qualifiedName.substringAfterLast(".")
    }

    /**
     * @return if [name] does contain a qualifier.
     */
    @JvmStatic
    fun isNameQualified(name: String): Boolean {
        return name.contains('.')
    }

    /**
     * Tries to get qualifiers before [usage] and merge them with [usage] text.
     */
    @JvmStatic
    fun getQualifiedNameFromUsage(usage: PsiElement): String {
        val firstQualifier = (usage.prevSibling as? FregeQualifier) ?: return usage.text
        val secondQualifier = (firstQualifier.prevSibling as? FregeQualifier)
            ?: return "${firstQualifier.text}${usage.text}"
        return "${secondQualifier.text}${firstQualifier.text}${usage.text}"
    }

    /**
     * @return the prefix before the last '.' in [qualifiedName].
     */
    @JvmStatic
    fun qualifierFromQualifiedName(qualifiedName: String): String {
        return qualifiedName.substringBeforeLast(".", "")
    }

    /**
     * Merges full qualified name of class with name (qualified or not) of method or qualified data name.
     * Returns `null` if cannot merge.
     * Example: `frege.prelude.PreludeBase` merges with `PreludeBase.Int` -> `frege.prelude.PreludeBase.Int`.
     */
    @JvmStatic
    fun mergeQualifiedNames(first: String, second: String): String {
        val secondName = nameFromQualifiedName(second)
        val secondQualifier = qualifierFromQualifiedName(second)
        return if (qualifiedNameEndsWithQualifier(first, secondQualifier)) {
            "$first.$secondName" // TODO
        } else {
            "$first.$second"
        }
    }

    @JvmStatic
    private fun qualifiedNameEndsWithQualifier(qualifiedName: String, qualifier: String): Boolean {
        return if (!qualifiedName.endsWith(qualifier)) {
            false
        } else if (qualifiedName == qualifier) {
            true
        } else {
            qualifiedName[qualifiedName.length - qualifier.length - 1] == '.'
        }
    }

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
     * @return the previous siblings of the PSI element.
     */
    @JvmStatic
    fun siblingBackwardSequenceSkipping(
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
    fun isElementTypeWithinChildren(element: PsiElement, type: IElementType): Boolean {
        return generateSequence({ element.firstChild }, { it.nextSibling }).any { it.elementType === type }
    }

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
}
