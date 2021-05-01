package com.plugin.frege.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.java.stubs.index.JavaFullClassNameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

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

}
