package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.FregeDataDclNative;
import com.plugin.frege.psi.FregeElementFactory;
import com.plugin.frege.psi.FregeTypes;
import com.plugin.frege.psi.impl.FregePsiClassImpl;
import com.plugin.frege.stubs.FregeClassStub;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class FregeDataNameNativeMixin extends FregePsiClassImpl implements PsiIdentifier {
    public FregeDataNameNativeMixin(@NotNull ASTNode node) {
        super(node);
    }

    public FregeDataNameNativeMixin(@NotNull FregeClassStub stub, @NotNull IStubElementType<?, ?> nodeType) {
        super(stub, nodeType);
    }

    @Override
    public @NotNull String getName() {
        return getText();
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public PsiMethod @NotNull [] getMethods() {
        return PsiMethod.EMPTY_ARRAY;
    }

    @Override
    public @NotNull PsiIdentifier getNameIdentifier() {
        return this;
    }

    @Override
    public @NotNull PsiElement getScope() {
        return Objects.requireNonNull(PsiTreeUtil.getParentOfType(this, FregeDataDclNative.class));
    }

    @Override
    public PsiElement setName(@NlsSafe @NotNull String name) throws IncorrectOperationException {
        return getNameIdentifier().replace(FregeElementFactory.createDataNameNative(getProject(), name));
    }

    @Override
    public IElementType getTokenType() {
        return FregeTypes.DATA_NAME_NATIVE;
    }
}
