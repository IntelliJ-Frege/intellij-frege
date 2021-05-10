package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightModifierList;
import com.intellij.psi.impl.source.HierarchicalMethodSignatureImpl;
import com.intellij.psi.impl.source.tree.java.PsiCodeBlockImpl;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.FregeLanguage;
import com.plugin.frege.psi.*;
import com.plugin.frege.psi.impl.FregePsiMethodImpl;
import com.plugin.frege.psi.impl.FregePsiUtilImpl;
import com.plugin.frege.resolve.FregeFunctionNameReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.plugin.frege.psi.FregeTypes.FUNCTION_NAME;

@SuppressWarnings("UnstableApiUsage")
public class FregeFunctionNameMixin extends FregePsiMethodImpl implements PsiIdentifier {

    private final LightModifierList modifierList;

    public FregeFunctionNameMixin(@NotNull ASTNode node) {
        super(node);
        modifierList = new LightModifierList(getManager(), FregeLanguage.INSTANCE,
                PsiModifier.STATIC, PsiModifier.FINAL, PsiModifier.PUBLIC); // TODO
    }

    @Override
    public @NotNull PsiType getReturnType() {
        return Objects.requireNonNull(getObjectType());
    }

    @Override
    public @Nullable PsiTypeElement getReturnTypeElement() {
        return getObjectTypeElement();
    }

    @Override
    public @NotNull PsiCodeBlock getBody() {
        FregeBinding binding = PsiTreeUtil.getParentOfType(this, FregeBinding.class);
        String text = binding == null ? getText() : binding.getText(); // TODO
        return new PsiCodeBlockImpl(text);
    }

    @Override
    public boolean isConstructor() {
        return false;
    }

    @Override
    public @NotNull PsiIdentifier getNameIdentifier() {
        return this;
    }

    @Override
    public @NotNull PsiModifierList getModifierList() {
        return modifierList;
    }

    @Override
    public PsiElement setName(@NlsSafe @NotNull String name) throws IncorrectOperationException {
        return getNameIdentifier().replace(FregeElementFactory.createFunctionName(getProject(), name));
    }

    @Override
    public @NotNull HierarchicalMethodSignature getHierarchicalMethodSignature() {
        return new HierarchicalMethodSignatureImpl(MethodSignatureBackedByPsiMethod.create(this, PsiSubstitutor.EMPTY));
    }

    @Override
    public PsiReference getReference() {
        return new FregeFunctionNameReference(this);
    }

    public boolean isFunctionBinding() {
        return getParent() instanceof FregeFunLhs;
    }

    public boolean isFunctionAnnotation() {
        return getParent() instanceof FregeAnnoItem;
    }

    @Override
    protected int getParamsNumber() {
        if (!isFunctionBinding()) {
            return 0; // TODO
        }

        PsiElement fregeLhs = Objects.requireNonNull(PsiTreeUtil.getParentOfType(this, FregeLhs.class));
        return PsiTreeUtil.findChildrenOfType(fregeLhs, FregeParam.class).size();
    }

    public boolean isMainFunctionBinding() {
        int argsCount = getParamsNumber();
        return isFunctionBinding()
                && argsCount <= 1
                && FregePsiUtilImpl.isInGlobalScope(this)
                && getText().equals("main");
    }

    @Override
    public IElementType getTokenType() {
        return FUNCTION_NAME;
    }
}
