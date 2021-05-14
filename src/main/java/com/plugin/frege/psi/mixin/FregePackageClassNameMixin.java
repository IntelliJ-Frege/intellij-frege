package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.*;
import com.plugin.frege.psi.impl.FregePsiClassImpl;
import com.plugin.frege.psi.impl.FregePsiUtilImpl;
import com.plugin.frege.resolve.FregePackageClassNameReference;
import com.plugin.frege.stubs.FregeClassStub;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.plugin.frege.psi.FregeTypes.PACKAGE_CLASS_NAME;

@SuppressWarnings("UnstableApiUsage")
public class FregePackageClassNameMixin extends FregePsiClassImpl implements PsiIdentifier {
    public FregePackageClassNameMixin(@NotNull ASTNode node) {
        super(node);
    }

    public FregePackageClassNameMixin(@NotNull FregeClassStub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
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
    public PsiMethod @NotNull [] getMethods() {
        FregeProgram program = PsiTreeUtil.getParentOfType(this, FregeProgram.class);
        if (program == null) {
            throw new IllegalStateException("Package must have a program above.");
        }
        FregeBody body = program.getBody();
        if (body == null) {
            return PsiMethod.EMPTY_ARRAY;
        }

        return FregePsiUtilImpl.subprogramsFromScopeOfElement(body, decl ->
                    decl instanceof FregeDecl ? ((FregeDecl) decl).getBinding() : null).stream()
                .map(FregeBinding::getLhs)
                .map(FregeLhs::getFunLhs).filter(Objects::nonNull)
                .map(FregeFunLhs::getFunctionName).filter(Objects::nonNull)
                .toArray(PsiMethod[]::new);
    }

    @Override
    public PsiElement setName(@NlsSafe @NotNull String name) throws IncorrectOperationException {
        return null; // TODO
    }

    @Override
    public PsiElement getScope() {
        return PsiTreeUtil.getParentOfType(this, FregeProgram.class);
    }

    @Override
    public @NotNull PsiReference getReference() {
        return new FregePackageClassNameReference(this);
    }

    @Override
    public IElementType getTokenType() {
        return PACKAGE_CLASS_NAME;
    }
}
