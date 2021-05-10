package com.plugin.frege.psi;

import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.facade.JvmElementProvider;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.FregeFileType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class FregeJvmElementProviderFromLibraries implements JvmElementProvider {
    @Override
    public @NotNull List<? extends JvmClass> getClasses(@NotNull String qualifiedName, @NotNull GlobalSearchScope scope) {
        if (!scope.isSearchInLibraries()) {
            return List.of();
        }

        Project project = scope.getProject();
        if (project == null) {
            return List.of();
        }

        PsiManager manager = PsiManager.getInstance(project);
        List<PsiClass> classes = new ArrayList<>();
        DumbService.getInstance(project).runReadActionInSmartMode(() -> {
            ProjectFileIndex index = ProjectFileIndex.SERVICE.getInstance(project);
            Collection<VirtualFile> files = FileTypeIndex.getFiles(FregeFileType.INSTANCE, scope);
            for (VirtualFile fileOrDir : files) {
                if (fileOrDir.isDirectory() || !scope.contains(fileOrDir) || !index.isInLibrary(fileOrDir)) {
                    continue;
                }

                PsiFile file = manager.findFile(fileOrDir);
                PsiTreeUtil.findChildrenOfType(file, FregePsiClass.class).stream()
                        .filter(clazz -> Objects.equals(clazz.getQualifiedName(), qualifiedName))
                        .forEach(classes::add);
            }
        });

        return classes;
    }
}
