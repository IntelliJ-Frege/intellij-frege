package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.FregePackageName;
import com.plugin.frege.psi.FregeProgram;
import com.plugin.frege.psi.impl.FregePsiClassImpl;
import com.plugin.frege.resolve.FregePackageClassNameReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.plugin.frege.psi.FregeTypes.PACKAGE_CLASS_NAME;

@SuppressWarnings("UnstableApiUsage")
public class FregePackageClassNameMixin extends FregePsiClassImpl implements PsiIdentifier {
    public FregePackageClassNameMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull PsiIdentifier getNameIdentifier() {
        return this;
    }

    @Override
    public @NotNull @NlsSafe String getQualifiedName() {
        FregePackageName packageName = PsiTreeUtil.getParentOfType(this, FregePackageName.class);
        if (packageName == null) {
            return getText();
        } else {
            return packageName.getText();
        }
    }

    @Override
    public @NotNull @NlsSafe String getName() {
        return getText();
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public @Nullable PsiElement getLBrace() {
        return getScope();
    }

    @Override
    public @Nullable PsiElement getRBrace() {
        return getScope().getLastChild();
    }

    @Override
    public PsiElement setName(@NlsSafe @NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement getScope() {
        return PsiTreeUtil.getParentOfType(this, FregeProgram.class);
    }

    @Override
    public @Nullable PsiClass getContainingClass() {
        return null;
    }

    @Override
    public @Nullable PsiReference getReference() {
        return new FregePackageClassNameReference(this);
    }

    @Override
    public IElementType getTokenType() {
        return PACKAGE_CLASS_NAME;
    }
}
