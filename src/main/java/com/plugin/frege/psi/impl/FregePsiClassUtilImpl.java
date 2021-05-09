package com.plugin.frege.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.java.stubs.index.JavaFullClassNameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.psi.FregePsiClassHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FregePsiClassUtilImpl {
    private FregePsiClassUtilImpl() {}

    /**
     * Returns a list of classes in the passed project with the passed full qualified name.
     */
    public static @NotNull List<PsiElement> getClassesByQualifiedName(@NotNull Project project,
                                                                      @NotNull String qualifiedName) {
        return JavaFullClassNameIndex.getInstance()
                .get(qualifiedName.hashCode(), project, GlobalSearchScope.everythingScope(project)).stream()
                .filter(psiClass -> Objects.equals(psiClass.getQualifiedName(), qualifiedName)).collect(Collectors.toList());
    }

    /**
     * Returns a list of methods and fields with the passed name in the passed class.
     */
    public static @NotNull List<PsiElement> getMethodsAndFieldsByName(@NotNull PsiClass psiClass,
                                                                      @NotNull String name) {
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
    public static @Nullable FregePsiClass findContainingClass(@NotNull PsiElement element) {
        FregePsiClassHolder holder = PsiTreeUtil.getParentOfType(element, FregePsiClassHolder.class);

        if (holder == null) {
            return null;
        }

        if (element instanceof FregePsiClass) { // in order not to return the same class
            PsiElement parent = holder.getParent();
            return parent == null ? null : findContainingClass(parent);
        }

        return holder.getHoldingClass();
    }
}
