package com.plugin.frege.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightParameter;
import com.intellij.psi.impl.light.LightParameterListBuilder;
import com.intellij.psi.impl.light.LightReferenceListBuilder;
import com.intellij.psi.impl.light.LightTypeElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.MethodSignature;
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.FregeLanguage;
import com.plugin.frege.psi.FregeBinding;
import com.plugin.frege.psi.FregePsiMethod;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public abstract class FregePsiMethodImpl extends FregeNamedElementImpl implements FregePsiMethod {
    static private PsiType objectType = null;
    private LightTypeElement objectTypeElement = null;

    public FregePsiMethodImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull PsiReferenceList getThrowsList() {
        return new LightReferenceListBuilder(getManager(), FregeLanguage.INSTANCE, PsiReferenceList.Role.THROWS_LIST);
    }

    @Override
    public boolean isVarArgs() {
        return false;
    }

    @Override
    public PsiMethod @NotNull [] findSuperMethods() {
        return PsiMethod.EMPTY_ARRAY; // TODO
    }

    @Override
    public PsiMethod @NotNull [] findSuperMethods(boolean checkAccess) {
        return PsiMethod.EMPTY_ARRAY; // TODO
    }

    @Override
    public PsiMethod @NotNull [] findSuperMethods(PsiClass parentClass) {
        return PsiMethod.EMPTY_ARRAY; // TODO
    }

    @Override
    public @NotNull List<MethodSignatureBackedByPsiMethod> findSuperMethodSignaturesIncludingStatic(boolean checkAccess) {
        return List.of(); // TODO
    }

    @Override
    public @Nullable PsiMethod findDeepestSuperMethod() {
        return null; // TODO
    }

    @Override
    public PsiMethod @NotNull [] findDeepestSuperMethods() {
        return PsiMethod.EMPTY_ARRAY; // TODO
    }

    @Override
    public boolean isDeprecated() {
        return false; // TODO
    }

    @Override
    public @Nullable PsiDocComment getDocComment() {
        return null; // TODO
    }

    @Override
    public @Nullable PsiClass getContainingClass() {
        return FregePsiClassUtilImpl.findContainingFregeClass(this);
    }

    @Override
    public boolean hasTypeParameters() {
        return false; // Unless we want to support generics
    }

    @Override
    public @Nullable PsiTypeParameterList getTypeParameterList() {
        return null; // Unless we want to support generics
    }

    @Override
    public PsiTypeParameter @NotNull [] getTypeParameters() {
        return PsiTypeParameter.EMPTY_ARRAY; // Unless we want to support generics
    }

    @Override
    public @NotNull PsiParameterList getParameterList() {
        LightParameterListBuilder list = new LightParameterListBuilder(getManager(), FregeLanguage.INSTANCE);
        int paramsNumber = getParamsNumber();
        PsiType object = Objects.requireNonNull(getObjectType());
        PsiElement scope = Objects.requireNonNull(PsiTreeUtil.getParentOfType(this, FregeBinding.class));
        for (int i = 0; i < paramsNumber; i++) {
            list.addParameter(new LightParameter("arg" + i, object, scope));
        }

        return list; // TODO a normal type system
    }

    @Override
    public @NotNull MethodSignature getSignature(@NotNull PsiSubstitutor substitutor) {
        return MethodSignatureBackedByPsiMethod.create(this, substitutor);
    }

    @Override
    public boolean hasModifierProperty(@NonNls @NotNull String name) {
        return getModifierList().hasModifierProperty(name);
    }

    @Override
    public @NotNull String getName() {
        return Objects.requireNonNull(super.getName()); // only for making it @NotNull
    }

    protected abstract int getParamsNumber();

    protected @Nullable  PsiType getObjectType() {
        if (objectType == null) {
            objectType = PsiMethodReferenceType.getJavaLangObject(getManager(),
                    GlobalSearchScope.everythingScope(getProject()));
        }
        return objectType;
    }

    protected @Nullable PsiTypeElement getObjectTypeElement() {
        if (objectTypeElement == null) {
            PsiType object = getObjectType();
            if (object != null) {
                objectTypeElement = new LightTypeElement(getManager(), object);
            }
        }
        return objectTypeElement;
    }
}
