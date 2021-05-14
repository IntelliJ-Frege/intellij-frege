package com.plugin.frege.stubs.types;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.plugin.frege.psi.FregePsiMethod;
import com.plugin.frege.psi.impl.FregeFunctionNameImpl;
import com.plugin.frege.psi.impl.FregePsiUtilImpl;
import com.plugin.frege.stubs.FregeMethodStub;
import org.jetbrains.annotations.NotNull;

public class FregeFunctionNameElementType extends FregeMethodElementType {
    public FregeFunctionNameElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public FregePsiMethod createPsi(@NotNull FregeMethodStub stub) {
        return new FregeFunctionNameImpl(stub, this);
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        PsiElement element = node.getPsi();
        return FregePsiUtilImpl.isInGlobalScope(element);
    }

    @Override
    public @NotNull String getExternalId() {
        return super.getExternalId() + ".FUNCTION_NAME";
    }
}
