package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.plugin.frege.psi.FregeTypes;
import com.plugin.frege.psi.impl.FregeCompositeElementImpl;
import com.plugin.frege.resolve.FregeDataNameUsageReference;
import org.jetbrains.annotations.NotNull;

public class FregeDataNameUsageMixin extends FregeCompositeElementImpl implements PsiIdentifier {
    public FregeDataNameUsageMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new FregeDataNameUsageReference(this);
    }

    @Override
    public IElementType getTokenType() {
        return FregeTypes.DATA_NAME_USAGE;
    }
}
