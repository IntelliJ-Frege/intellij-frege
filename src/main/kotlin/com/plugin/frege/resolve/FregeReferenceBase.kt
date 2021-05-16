package com.plugin.frege.resolve

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache

abstract class FregeReferenceBase(@JvmField protected val psiElement: PsiElement, range: TextRange) :
    PsiReferenceBase<PsiElement?>(psiElement, range), PsiPolyVariantReference {

    protected abstract fun resolveInner(incompleteCode: Boolean): List<PsiElement>

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return ResolveCache.getInstance(psiElement.project).resolveWithCaching(
                this, { fregeReferenceBase, _ ->
                fregeReferenceBase.resolveInner(false)
                    .map { element -> PsiElementResolveResult(element) }
                    .toTypedArray()
            },
            true, false
        )
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }

    override fun getVariants(): Array<Any> {
        return resolveInner(true).toTypedArray()
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is FregeReferenceBase) {
            false
        } else psiElement == other.psiElement
    }

    override fun hashCode(): Int {
        return psiElement.hashCode()
    }
}
