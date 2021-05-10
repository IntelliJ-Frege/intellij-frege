package com.plugin.frege.psi.impl;

import com.intellij.openapi.module.impl.scopes.LibraryScope;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.psi.FregePsiClassHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FregePsiClassUtilImpl {
    private FregePsiClassUtilImpl() {}

    /**
     * Returns a list of classes in the passed project with the passed full qualified name.
     */
    public static @NotNull List<PsiClass> getClassesByQualifiedName(@NotNull Project project, @NotNull String qualifiedName) {
        List<PsiClass> inProject = doFindClasses(qualifiedName, GlobalSearchScope.projectScope(project));
        if (!inProject.isEmpty()) {
            return inProject;
        } else {
            return doFindClasses(qualifiedName, LibraryScope.everythingScope(project));
        }
    }

    private static @NotNull List<PsiClass> doFindClasses(@NotNull String qualifiedName, @NotNull GlobalSearchScope scope) {
        if (scope.getProject() == null) {
            return List.of();
        }

        PsiClass[] classes = JavaPsiFacade.getInstance(scope.getProject()).findClasses(qualifiedName, scope);
        if (classes.length > 0) {
            return Arrays.stream(classes).collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    /**
     * Returns a list of methods and fields with the passed name in the passed class.
     */
    public static @NotNull List<PsiElement> getMethodsAndFieldsByName(@NotNull PsiClass psiClass, @NotNull String name) {
        PsiMethod[] methods = psiClass.findMethodsByName(name, true);
        if (methods.length != 0) {
            return Arrays.stream(methods).collect(Collectors.toList());
        }

        PsiField field = psiClass.findFieldByName(name, true);
        if (field != null) {
            return List.of(field);
        } else {
            return List.of();
        }
    }

    /**
     * Returns the nearest containing class of the passed element.
     *
     * See {@link FregePsiClassHolder}.
     */
    public static @Nullable FregePsiClass findContainingFregeClass(@NotNull PsiElement element) {
        FregePsiClassHolder holder = PsiTreeUtil.getParentOfType(element, FregePsiClassHolder.class);

        if (holder == null) {
            return null;
        }

        if (element instanceof FregePsiClass) { // in order not to return the same class
            PsiElement parent = holder.getParent();
            return parent == null ? null : findContainingFregeClass(parent);
        }

        return holder.getHoldingClass();
    }

    /**
     * Returns all methods in the passed project with the passed qualified name.
     */
    public static @NotNull List<@NotNull PsiMethod> getMethodsByQualifiedName(@NotNull Project project,
                                                                              @NotNull String qualifiedName) {
        String qualifier;
        String name;
        if (qualifiedName.contains(".")) {
            qualifier = qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
            if (qualifier.length() + 1 >= qualifiedName.length()) {
                return List.of();
            }
            name = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
        } else {
            return List.of();
        }

        return getClassesByQualifiedName(project, qualifier).
                stream().flatMap(clazz -> Arrays.stream(clazz.findMethodsByName(name, true))).
                collect(Collectors.toList());
    }
}
