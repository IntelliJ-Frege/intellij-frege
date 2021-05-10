package com.plugin.frege.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.psi.impl.FregePsiClassUtilImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class FregePsiElementFinder extends PsiElementFinder {
    @Override
    public @Nullable PsiClass findClass(@NotNull String qualifiedName, @NotNull GlobalSearchScope scope) {
        PsiClass[] classes = findClasses(qualifiedName, scope);
        if (classes.length == 0) {
            return null;
        }

        return classes[0];
    }

    @Override
    public PsiClass @NotNull [] findClasses(@NotNull String qualifiedName, @NotNull GlobalSearchScope scope) {
        return getClasses(clazz -> Objects.equals(clazz.getQualifiedName(), qualifiedName), scope,
                isInFregeLibrary(qualifiedName));
    }

    @Override
    public PsiClass @NotNull [] getClasses(@NotNull PsiPackage psiPackage, @NotNull GlobalSearchScope scope) {
        return getClasses(clazz -> {
            String clazzName = clazz.getName();
            return clazzName != null && psiPackage.containsClassNamed(clazzName);
        }, scope, isInFregeLibrary(psiPackage.getQualifiedName()));
    }

    private PsiClass @NotNull [] getClasses(@NotNull Predicate<PsiClass> predicate, @NotNull GlobalSearchScope scope,
                                            boolean includingLibrary) {
        Project project = scope.getProject();
        if (project == null) {
            return PsiClass.EMPTY_ARRAY;
        }

        PsiManager manager = PsiManager.getInstance(project);
        List<PsiClass> classes = new ArrayList<>();

        ContentIterator processor = virtualFile -> {
            PsiFile file = manager.findFile(virtualFile);
            PsiTreeUtil.findChildrenOfType(file, FregePsiClass.class).stream()
                    .filter(predicate).forEach(classes::add);
            return true;
        };
        VirtualFileFilter filter = file -> true;
        FregePsiClassUtilImpl.iterateFregeFiles(processor, scope, filter, includingLibrary);

        return classes.toArray(PsiClass[]::new);
    }

    private boolean isInFregeLibrary(@NotNull String qualifiedName) {
        return qualifiedName.startsWith("frege");
    }
}
