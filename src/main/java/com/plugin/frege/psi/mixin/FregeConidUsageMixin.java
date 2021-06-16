package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.plugin.frege.psi.FregeResolvableElement;
import com.plugin.frege.psi.FregeTypes;
import com.plugin.frege.psi.impl.FregeCompositeElementImpl;
import com.plugin.frege.resolve.FregeConidUsageReference;
import org.jetbrains.annotations.NotNull;

public class FregeConidUsageMixin extends FregeCompositeElementImpl implements FregeResolvableElement, PsiIdentifier {
    public FregeConidUsageMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new FregeConidUsageReference(this);
    }

    @Override
    public IElementType getTokenType() {
        return FregeTypes.CONID_USAGE;
    }
}
