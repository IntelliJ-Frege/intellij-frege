package com.plugin.frege.psi.impl;

import com.intellij.openapi.module.impl.scopes.LibraryScope;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.FregeFileType;
import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.psi.FregePsiClassHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
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
        String name = FregePsiUtilImpl.nameFromQualifiedName(qualifiedName);
        String qualifier = FregePsiUtilImpl.qualifierFromQualifiedName(qualifiedName);
        if (qualifier.isEmpty()) {
            return List.of();
        }

        return getClassesByQualifiedName(project, qualifier).
                stream().flatMap(clazz -> Arrays.stream(clazz.findMethodsByName(name, true))).
                collect(Collectors.toList());
    }

    /**
     * @return all methods in the passed project in the passed import.
     */
    public static @NotNull List<@NotNull PsiMethod> getAllMethodsByImportName(@NotNull Project project,
                                                                              @NotNull String importName) {
        if (importName.isEmpty()) {
            return List.of();
        }
        return getClassesByQualifiedName(project, importName).
                stream().flatMap(clazz -> Arrays.stream(clazz.getAllMethods()))
                .collect(Collectors.toList());
    }

    /**
     * Iterates over all the Frege files in the passed scope and filters with the passed filter.
     * After that applies the passed processor.
     */
    public static void iterateFregeFiles(@NotNull ContentIterator processor, @NotNull GlobalSearchScope scope,
                                         @NotNull VirtualFileFilter filter, boolean includingLibraries) {
        Project project = scope.getProject();
        if (project == null) {
            return;
        }

        DumbService.getInstance(project).runReadActionInSmartMode(() -> {
            if (includingLibraries) {
                Collection<VirtualFile> files = FileTypeIndex.getFiles(FregeFileType.INSTANCE, scope);
                for (VirtualFile virtualFile : files) {
                    if (filter.accept(virtualFile) && !processor.processFile(virtualFile)) {
                        break;
                    }
                }
            } else {
                ProjectFileIndex.getInstance(project).iterateContent(processor, filter);
            }
        });
    }
}
