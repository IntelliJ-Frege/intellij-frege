package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.FregeDataDclNative;
import com.plugin.frege.psi.FregeElementFactory;
import com.plugin.frege.psi.FregeTypes;
import com.plugin.frege.psi.impl.FregePsiClassImpl;
import com.plugin.frege.resolve.FregeDataNameNativeReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class FregeDataNameNativeMixin extends FregePsiClassImpl implements PsiIdentifier {
    public FregeDataNameNativeMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @Nullable @NlsSafe String getQualifiedName() {
        PsiClass containingClass = getContainingClass();
        if (containingClass == null) {
            return null;
        }

        String parentQualifiedName = containingClass.getQualifiedName();
        if (parentQualifiedName == null) {
            return null;
        }

        return parentQualifiedName + "." + getName();
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
    public PsiReference getReference() {
        return new FregeDataNameNativeReference(this);
    }

    @Override
    public IElementType getTokenType() {
        return FregeTypes.DATA_NAME_NATIVE;
    }
}
