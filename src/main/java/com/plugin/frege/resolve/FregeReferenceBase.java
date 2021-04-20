package com.plugin.frege.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class FregeReferenceBase extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private final PsiElement element;

    public FregeReferenceBase(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        this.element = element;
    }

    protected abstract List<PsiElement> resolveInner(boolean incompleteCode);

    @Override
    final public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        return ResolveCache.getInstance(element.getProject())
                .resolveWithCaching(this, (fregeReferenceBase, incompleteCodeUnused) ->
                                fregeReferenceBase.resolveInner(false).stream()
                                        .map(PsiElementResolveResult::new)
                                        .toArray(PsiElementResolveResult[]::new),
                        true, false);
    }

    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        if (resolveResults.length == 1) {
            return resolveResults[0].getElement();
        } else {
            return null;
        }
    }

    @Override
    public Object @NotNull [] getVariants() {
        return resolveInner(true).toArray();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof FregeReferenceBase)) {
            return false;
        }
        return element.equals(((FregeReferenceBase) other).element);
    }

    @Override
    public int hashCode() {
        return element.hashCode();
    }
}
