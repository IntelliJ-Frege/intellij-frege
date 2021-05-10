package com.plugin.frege.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightModifierList;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.scope.NameHint;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.plugin.frege.FregeLanguage;
import com.plugin.frege.psi.FregePsiClass;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class FregePsiClassImpl extends FregeNamedElementImpl implements FregePsiClass {

    private final LightModifierList modifierList;

    public FregePsiClassImpl(@NotNull ASTNode node) {
        super(node);
        modifierList = new LightModifierList(getManager(), FregeLanguage.INSTANCE,
                PsiModifier.PUBLIC, PsiModifier.FINAL); // TODO
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
        return null; // TODO
    }

    @Override
    public @Nullable PsiReferenceList getImplementsList() {
        return null; // TODO
    }

    @Override
    public PsiClassType @NotNull [] getExtendsListTypes() {
        return PsiClassType.EMPTY_ARRAY; // TODO
    }

    @Override
    public PsiClassType @NotNull [] getImplementsListTypes() {
        return PsiClassType.EMPTY_ARRAY; // TODO
    }

    @Override
    public @Nullable PsiClass getSuperClass() {
        return null; // TODO (or always null?)
    }

    @Override
    public PsiClass @NotNull [] getInterfaces() {
        return PsiClass.EMPTY_ARRAY; // TODO
    }

    @Override
    public PsiClass @NotNull [] getSupers() {
        return PsiClass.EMPTY_ARRAY; // TODO
    }

    @Override
    public PsiClassType @NotNull [] getSuperTypes() {
        return PsiClassType.EMPTY_ARRAY; // TODO
    }

    @Override
    public PsiField @NotNull [] getFields() {
        return PsiField.EMPTY_ARRAY; // TODO (figure out when functions become fields)
    }

    @Override
    public PsiMethod @NotNull [] getConstructors() {
        return PsiMethod.EMPTY_ARRAY; // TODO
    }

    @Override
    public PsiClass @NotNull [] getInnerClasses() {
        return PsiClass.EMPTY_ARRAY; // TODO (or always null?)
    }

    @Override
    public PsiClassInitializer @NotNull [] getInitializers() {
        return PsiClassInitializer.EMPTY_ARRAY; // TODO (or always null)
    }

    @Override
    public PsiField @NotNull [] getAllFields() {
        return PsiField.EMPTY_ARRAY; // TODO
    }

    @Override
    public PsiMethod @NotNull [] getAllMethods() {
        return getMethods(); // TODO
    }

    @Override
    public PsiClass @NotNull [] getAllInnerClasses() {
        return PsiClass.EMPTY_ARRAY; // TODO
    }

    @Override
    public @Nullable PsiField findFieldByName(@NonNls String name, boolean checkBases) {
        return null; // TODO
    }

    @Override
    public @Nullable PsiMethod findMethodBySignature(PsiMethod patternMethod, boolean checkBases) {
        PsiMethod[] methods = findMethodsBySignature(patternMethod, checkBases);
        if (methods.length > 0) {
            return methods[0];
        } else {
            return null;
        }
    }

    @Override
    public PsiMethod @NotNull [] findMethodsBySignature(PsiMethod patternMethod, boolean checkBases) {
        PsiMethod[] allMethods = checkBases ? getAllMethods() : getMethods();
        return Arrays.stream(allMethods).filter(method ->
                Arrays.equals(patternMethod.getSignature(EmptySubstitutor.getInstance()).getParameterTypes(), method.getSignature(EmptySubstitutor.getInstance()).getParameterTypes()))
                .toArray(PsiMethod[]::new);
    }

    @Override
    public PsiMethod @NotNull [] findMethodsByName(@NonNls String name, boolean checkBases) {
        PsiMethod[] allMethods = checkBases ? getAllMethods() : getMethods();
        return Arrays.stream(allMethods).filter(method ->
                method.getName().equals(name)).toArray(PsiMethod[]::new);
    }

    @Override
    public @NotNull List<Pair<PsiMethod, PsiSubstitutor>> findMethodsAndTheirSubstitutorsByName(@NonNls String name, boolean checkBases) {
        PsiMethod[] methods = findMethodsByName(name, checkBases);
        return Arrays.stream(methods)
                .map(method -> new Pair<>(method, EmptySubstitutor.EMPTY))
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull List<Pair<PsiMethod, PsiSubstitutor>> getAllMethodsAndTheirSubstitutors() {
        return Arrays.stream(getAllMethods())
                .map(method -> new Pair<>(method, EmptySubstitutor.EMPTY))
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable PsiClass findInnerClassByName(@NonNls String name, boolean checkBases) {
        return null; // TODO
    }

    @Override
    public boolean isInheritor(@NotNull PsiClass baseClass, boolean checkDeep) {
        return false; // TODO
    }

    @Override
    public boolean isInheritorDeep(PsiClass baseClass, @Nullable PsiClass classToByPass) {
        return false; // TODO
    }

    @Override
    public @NotNull Collection<HierarchicalMethodSignature> getVisibleSignatures() {
        return Arrays.stream(getAllMethods()).map(PsiMethod::getHierarchicalMethodSignature).collect(Collectors.toList());
    }

    @Override
    public boolean isDeprecated() {
        return false; // TODO
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
    public @Nullable PsiElement getLBrace() {
        return getScope();
    }

    @Override
    public @Nullable PsiElement getRBrace() {
        return getScope().getLastChild();
    }

    @Override
    public @Nullable PsiDocComment getDocComment() {
        return null; // TODO
    }

    @Override
    public @NotNull PsiModifierList getModifierList() {
        return modifierList;
    }

    @Override
    public boolean hasModifierProperty(@NonNls @NotNull String name) {
        return getModifierList().hasModifierProperty(name); // TODO
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        NameHint nameHint = processor.getHint(NameHint.KEY);
        String name = nameHint == null ? null : nameHint.getName(state);
        PsiMethod[] methods = name == null ? getAllMethods() : findMethodsByName(name, true);
        for (PsiMethod method : methods) {
            processor.execute(method, state);
        }

        return false; // TODO fields
    }

    @Override
    public @Nullable PsiClass getContainingClass() {
        return FregePsiClassUtilImpl.findContainingFregeClass(this);
    }
}
