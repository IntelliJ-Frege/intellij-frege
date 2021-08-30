package com.plugin.frege.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.parentOfType
import com.intellij.util.castSafelyTo
import com.plugin.frege.psi.*
import com.plugin.frege.psi.impl.FregeCompositeElementImpl
import com.plugin.frege.psi.util.FregePsiUtil.isNameQualified
import com.plugin.frege.resolve.FregeReferenceBase
import com.plugin.frege.resolve.FregeResolveUtil.findMethodsAndFieldsByName

open class FregeNativeFunctionNameMixin(node: ASTNode) : FregeCompositeElementImpl(node), PsiIdentifier {
    override fun getReference(): PsiReference? {
        return object : FregeReferenceBase(this, TextRange(0, textLength)) {
            override fun resolveInner(incompleteCode: Boolean): List<PsiElement> {
                val function = psiElement.parentOfType<FregeNativeFunction>()
                return if (function != null) listOf(function) else emptyList()
            }

            override fun handleElementRename(name: String): PsiElement {
                return psiElement.replace(FregeElementFactory.createNativeFunctionName(psiElement.project, name))
            }
        }
    }

    override fun getTokenType(): IElementType = FregeTypes.NATIVE_FUNCTION_NAME

    fun getDelegatedMember(): PsiMember? {
        val name = text
        if (name == CONSTRUCTOR_NAME) {
            return tryResolveConstructor()
        }
        return parentOfType<FregeNativeFunction>()?.let { nativeFunction ->
            nativeFunction.javaItem?.let { return tryResolveMemberFromJavaItem(it) }
            (nativeFunction.containingClass as? FregeNativeDataDecl)?.let { containingClass ->
                tryResolveFromClass(containingClass, name)
            }
        }
    }

    private fun tryResolveConstructor(): PsiMethod? {
        val containingClass = parentOfType<FregeNativeDataDecl>() ?: return null
        return (resolveReferenceFromNativeName(containingClass.nativeName) as? PsiClass)
            ?.constructors?.firstOrNull()
    }

    private fun tryResolveMemberFromJavaItem(javaItem: FregeJavaItem?): PsiMember? {
        val nativeName = javaItem?.nativeName ?: return null
        val text = nativeName.text
        if (isNameQualified(text)) {
            val resolvedFromJavaItem = resolveReferenceFromNativeName(nativeName)
            if (resolvedFromJavaItem is PsiMethod || resolvedFromJavaItem is PsiField) {
                return resolvedFromJavaItem
            }
        } else {
            javaItem.parentOfType<FregeNativeDataDecl>()?.let { containingClass ->
                return tryResolveFromClass(containingClass, text)
            }
        }
        return null
    }

    private fun tryResolveFromClass(clazz: FregeNativeDataDecl, memberName: String): PsiMember? {
        return (resolveReferenceFromNativeName(clazz.nativeName) as? PsiClass)?.let { javaClass ->
            findMethodsAndFieldsByName(javaClass, memberName).firstOrNull()
        }
    }

    private fun resolveReferenceFromNativeName(nativeName: FregeNativeName?): PsiMember? {
        return nativeName?.reference?.castSafelyTo<PsiPolyVariantReference>()
            ?.multiResolve(false)?.mapNotNull { it.element }?.firstOrNull() as? PsiMember
    }

    private companion object {
        private const val CONSTRUCTOR_NAME = "new"
    }
}
