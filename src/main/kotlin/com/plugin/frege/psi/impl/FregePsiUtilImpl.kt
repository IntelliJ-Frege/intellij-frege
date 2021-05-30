package com.plugin.frege.psi.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.parentOfTypes
import com.plugin.frege.psi.*
import kotlin.reflect.KClass

object FregePsiUtilImpl {
    @JvmStatic
    private val defaultImports = listOf(
        "frege.Prelude" // TODO
    )

    @JvmStatic
    private fun isScope(element: PsiElement?): Boolean {
        return element is FregeScopeElement
    }

    /**
     * Finds the first parent of [element] that presents a scope.
     * @return the [element], if it is a scope or `null` if there is no a scope for [element].
     */
    @Suppress("RedundantNullableReturnType")
    @JvmStatic
    fun scopeOfElement(element: PsiElement): FregeScopeElement? {
        return element.parentOfTypes(FregeScopeElement::class, withSelf = true)
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
            scope!!.subprogramsFromScope.mapNotNull {getter(it) }
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
     * @return a predicate, accepting only [PsiElement] for which [PsiElement.getText] equals [text].
     */
    @JvmStatic
    fun keepWithText(text: String): (elem: PsiElement?) -> Boolean {
        return { it != null && text == it.text }
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
     * @return [FregeWhereSection] if the first found binging contains it or `null` it not.
     */
    @JvmStatic
    fun findWhereInExpression(element: PsiElement): FregeWhereSection? {
        val binding = element.parentOfType<FregeBinding>(false)
        val elements = findElementsWithinElementSequence(binding) { it === element }
        return if (elements.any()) {
            findElementsWithinElementSequence(binding) {
                it is FregeWhereSection
            }.firstOrNull() as FregeWhereSection?
        } else {
            null
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
        return isScopeGlobal(scopeOfElement(element))
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
            isScope(element) -> {
                emptySequence()
            }
            else -> {
                element.children.asSequence().flatMap { findElementsWithinElementSequence(it, predicate) }
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
     * @return list of [FregePsiClass] which are in the global scope of [element].
     */
    @JvmStatic
    fun findClassesInCurrentFile(element: PsiElement): List<FregePsiClass> {
        val globalScope = globalScopeOfElement(element) ?: return emptyList()
        check(globalScope is FregeBody) { "Global scope must be Frege body." }
        return globalScope.topDeclList
            .mapNotNull {
                when { // TODO omg, think up a better way
                    it.classDecl != null -> it.classDecl
                    it.dataDecl != null -> it.dataDecl
                    it.nativeDataDecl != null -> it.nativeDataDecl
                    it.typeDecl != null -> it.typeDecl
                    else -> null
                }
            }
            .filterIsInstance(FregePsiClass::class.java)
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
     * @return all the imports [element] can access to.
     * It doesn't return default imports such as `frege.Prelude`.
     */
    @JvmStatic
    fun findImportsForElement(element: PsiElement): List<FregeImportDecl> {
        val body = element.parentOfTypes(FregeBody::class, withSelf = true) ?: return emptyList()
        return body.topDeclList.asSequence()
            .map { it.importDecl }
            .filterNotNull().toList()
    }

    /**
     * @return import names from the file [element] contains in.
     * If [includingDefault] is `true`, it includes default imports such as `frege.Prelude`.
     */
    @JvmStatic
    fun findImportsNamesForElement(element: PsiElement, includingDefault: Boolean): List<String> {
        val imports = findImportsForElement(element)
            .mapNotNull { it.packageName?.text }
            .toMutableList()
        if (includingDefault) {
            imports.addAll(defaultImports)
        }
        return imports
    }

    /**
     * @return the last word after the last '.' in [qualifiedName].
     */
    @JvmStatic
    fun nameFromQualifiedName(qualifiedName: String): String {
        return qualifiedName.substringAfterLast(".")
    }

    /**
     * Tries to get qualifiers before [usage] and merge them with [usage] text.
     */
    @JvmStatic
    fun getQualifiedNameFromUsage(usage: PsiElement): String {
        val firstQualifier = usage.prevSibling ?: return usage.text
        val secondQualifier = firstQualifier.prevSibling ?: return "${firstQualifier.text}${usage.text}"
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
     * @return the first parent that inherits [FregeDecl], or `null` if none of them found
     */
    @JvmStatic
    fun getDeclType(element: PsiElement): PsiElement? {
        val fregeDeclParent = element.parentOfTypes(FregeDecl::class, withSelf = false)
        return fregeDeclParent?.firstChild
    }
}
