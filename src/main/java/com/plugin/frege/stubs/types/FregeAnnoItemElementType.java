package com.plugin.frege.stubs.types;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.plugin.frege.psi.FregeClassDcl;
import com.plugin.frege.psi.FregePsiMethod;
import com.plugin.frege.psi.impl.FregeAnnoItemImpl;
import com.plugin.frege.stubs.FregeMethodStub;
import org.jetbrains.annotations.NotNull;

public class FregeAnnoItemElementType extends FregeMethodElementType {
    public FregeAnnoItemElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public @NotNull String getExternalId() {
        return super.getExternalId() + ".ANNO_ITEM";
    }

    @Override
    public FregePsiMethod createPsi(@NotNull FregeMethodStub stub) {
        return new FregeAnnoItemImpl(stub, this);
    }

    // a workaround in order not to consider all annotations as methods
    @Override
    public boolean shouldCreateStub(ASTNode node) {
        PsiElement element = node.getPsi();
        if (!(element instanceof FregeAnnoItemImpl)) {
            return true;
        }
        FregeAnnoItemImpl annotation = (FregeAnnoItemImpl) element;
        return annotation.getContainingClass() instanceof FregeClassDcl;
    }
}
