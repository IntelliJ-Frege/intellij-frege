package com.plugin.frege.stubs.types;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.plugin.frege.psi.FregePsiMethod;
import com.plugin.frege.psi.impl.FregeBindingImpl;
import com.plugin.frege.psi.impl.FregePsiUtilImpl;
import com.plugin.frege.stubs.FregeMethodStub;
import org.jetbrains.annotations.NotNull;

public class FregeBindingElementType extends FregeMethodElementType {
    public FregeBindingElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public FregePsiMethod createPsi(@NotNull FregeMethodStub stub) {
        return new FregeBindingImpl(stub, this);
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        PsiElement element = node.getPsi();
        return FregePsiUtilImpl.isInGlobalScope(element);
    }

    @Override
    public @NotNull String getExternalId() {
        return super.getExternalId() + ".BINDING";
    }
}
