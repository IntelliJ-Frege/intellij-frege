package com.plugin.frege.resolve

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.plugin.frege.psi.FregeBinding
import com.plugin.frege.psi.impl.FregePsiClassUtilImpl.getAllMethodsByImportName
import com.plugin.frege.psi.impl.FregePsiClassUtilImpl.getMethodsByQualifiedName
import com.plugin.frege.psi.impl.FregePsiUtilImpl.findClassesInCurrentFile
import com.plugin.frege.psi.impl.FregePsiUtilImpl.findElementsWithinScope
import com.plugin.frege.psi.impl.FregePsiUtilImpl.findImportsNamesForElement
import com.plugin.frege.psi.impl.FregePsiUtilImpl.findWhereInExpression
import com.plugin.frege.psi.impl.FregePsiUtilImpl.getByTypePredicateCheckingName
import com.plugin.frege.psi.impl.FregePsiUtilImpl.mergeQualifiedNames
import com.plugin.frege.psi.impl.FregePsiUtilImpl.scopeOfElement

object FregeResolveUtil {
    @JvmStatic
    fun findMethodsFromUsage(
        usage: PsiElement,
        incompleteCode: Boolean
    ): List<PsiElement> {
        val result = findBindings(usage, incompleteCode).toMutableList()
        if (result.isNotEmpty() && !incompleteCode) {
            return result
        }

        result.addAll(findMethodsInClassesInCurrentFile(usage, incompleteCode))
        if (result.isNotEmpty() && !incompleteCode) {
            return result
        }

        result.addAll(findMethodsByImports(usage, incompleteCode))
        return result
    }

    private fun findMethodsByImports(
        usage: PsiElement,
        incompleteCode: Boolean
    ): List<PsiMethod> {
        val name = usage.text
        val project = usage.project
        val imports = findImportsNamesForElement(usage, true)
        val methods = mutableListOf<PsiMethod>()
        for (currentImport in imports) {
            if (incompleteCode) {
                methods.addAll(getAllMethodsByImportName(project, currentImport))
            } else {
                val qualifiedName = mergeQualifiedNames(currentImport, name)
                methods.addAll(getMethodsByQualifiedName(project, qualifiedName))
                if (methods.isNotEmpty()) {
                    break
                }
            }
        }
        return methods
    }

    private fun findMethodsInClassesInCurrentFile(
        usage: PsiElement,
        incompleteCode: Boolean // TODO
    ): List<PsiMethod> {
        val name = usage.text
        val project = usage.project
        val availableClasses = findClassesInCurrentFile(usage)
        for (clazz in availableClasses) {
            val className = clazz.qualifiedName ?: continue
            val qualifiedName = mergeQualifiedNames(className, name)
            val methods = getMethodsByQualifiedName(project, qualifiedName)
            if (methods.isNotEmpty()) {
                return methods // TODO errors if several references in different classes
            }
        }

        return emptyList()
    }

    private fun findBindings(
        usage: PsiElement,
        incompleteCode: Boolean
    ): List<PsiElement> {
        val name = usage.text
        val predicate = getByTypePredicateCheckingName(FregeBinding::class, name, incompleteCode)

        // check if this expression has `where` ans search there for definitions if it does.
        val where = findWhereInExpression(usage)
        if (where?.indentSection != null) {
            val whereFuncNames = findElementsWithinScope(where.indentSection!!, predicate)
            if (whereFuncNames.isNotEmpty()) {
                return whereFuncNames
            }
        }

        // search for definitions in the current and outer scopes
        var scope: PsiElement? = scopeOfElement(usage)
        while (scope != null) {
            val functionNames = findElementsWithinScope(scope, predicate)
            if (functionNames.isNotEmpty()) {
                return listOf(functionNames.minByOrNull { it.textOffset }!!)
            }
            scope = scopeOfElement(scope.parent)
        }
        return emptyList()
    }
}