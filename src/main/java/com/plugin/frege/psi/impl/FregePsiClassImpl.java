package com.plugin.frege.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.plugin.frege.psi.FregePsiClass;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public abstract class FregePsiClassImpl extends FregeNamedElementImpl implements FregePsiClass {
    public FregePsiClassImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isAnnotationType() {
        return false;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public @Nullable PsiReferenceList getExtendsList() {
        return null;
    }

    @Override
    public @Nullable PsiReferenceList getImplementsList() {
        return null;
    }

    @Override
    public PsiClassType @NotNull [] getExtendsListTypes() {
        return new PsiClassType[0];
    }

    @Override
    public PsiClassType @NotNull [] getImplementsListTypes() {
        return new PsiClassType[0];
    }

    @Override
    public @Nullable PsiClass getSuperClass() {
        return null;
    }

    @Override
    public PsiClass @NotNull [] getInterfaces() {
        return new PsiClass[0];
    }

    @Override
    public PsiClass @NotNull [] getSupers() {
        return new PsiClass[0];
    }

    @Override
    public PsiClassType @NotNull [] getSuperTypes() {
        return new PsiClassType[0];
    }

    @Override
    public PsiField @NotNull [] getFields() {
        return new PsiField[0];
    }

    @Override
    public PsiMethod @NotNull [] getMethods() {
        return new PsiMethod[0];
    }

    @Override
    public PsiMethod @NotNull [] getConstructors() {
        return new PsiMethod[0];
    }

    @Override
    public PsiClass @NotNull [] getInnerClasses() {
        return new PsiClass[0];
    }

    @Override
    public PsiClassInitializer @NotNull [] getInitializers() {
        return new PsiClassInitializer[0];
    }

    @Override
    public PsiField @NotNull [] getAllFields() {
        return new PsiField[0];
    }

    @Override
    public PsiMethod @NotNull [] getAllMethods() {
        return new PsiMethod[0];
    }

    @Override
    public PsiClass @NotNull [] getAllInnerClasses() {
        return new PsiClass[0];
    }

    @Override
    public @Nullable PsiField findFieldByName(@NonNls String name, boolean checkBases) {
        return null;
    }

    @Override
    public @Nullable PsiMethod findMethodBySignature(PsiMethod patternMethod, boolean checkBases) {
        return null;
    }

    @Override
    public PsiMethod @NotNull [] findMethodsBySignature(PsiMethod patternMethod, boolean checkBases) {
        return new PsiMethod[0];
    }

    @Override
    public PsiMethod @NotNull [] findMethodsByName(@NonNls String name, boolean checkBases) {
        return new PsiMethod[0];
    }

    @Override
    public @NotNull List<Pair<PsiMethod, PsiSubstitutor>> findMethodsAndTheirSubstitutorsByName(@NonNls String name, boolean checkBases) {
        return List.of();
    }

    @Override
    public @NotNull List<Pair<PsiMethod, PsiSubstitutor>> getAllMethodsAndTheirSubstitutors() {
        return List.of();
    }

    @Override
    public @Nullable PsiClass findInnerClassByName(@NonNls String name, boolean checkBases) {
        return null;
    }

    @Override
    public boolean isInheritor(@NotNull PsiClass baseClass, boolean checkDeep) {
        return false;
    }

    @Override
    public boolean isInheritorDeep(PsiClass baseClass, @Nullable PsiClass classToByPass) {
        return false;
    }

    @Override
    public @NotNull Collection<HierarchicalMethodSignature> getVisibleSignatures() {
        return List.of();
    }

    @Override
    public boolean isDeprecated() {
        return false;
    }

    @Override
    public boolean hasTypeParameters() {
        return false;
    }

    @Override
    public @Nullable PsiTypeParameterList getTypeParameterList() {
        return null;
    }

    @Override
    public PsiTypeParameter @NotNull [] getTypeParameters() {
        return new PsiTypeParameter[0];
    }

    @Override
    public @Nullable PsiDocComment getDocComment() {
        return null;
    }

    @Override
    public @Nullable PsiModifierList getModifierList() {
        return null;
    }

    @Override
    public boolean hasModifierProperty(@NonNls @NotNull String name) {
        return false;
    }
}
