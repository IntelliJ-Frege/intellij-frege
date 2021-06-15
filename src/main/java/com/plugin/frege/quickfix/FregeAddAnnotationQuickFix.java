package com.plugin.frege.quickfix;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.FregeElementFactory;
import com.plugin.frege.psi.impl.FregePsiUtilImpl;
import com.plugin.frege.typesystem.FregeTypeSystemUtilsJavaSupport;
import com.plugin.frege.typesystem.TypeSystemException;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FregeAddAnnotationQuickFix extends BaseIntentionAction {
    private final PsiElement toAnnotate;
    private final PsiElement toAddBefore;

    public FregeAddAnnotationQuickFix(PsiElement toAnnotate, PsiElement toAddBefore) {
        this.toAnnotate = toAnnotate;
        this.toAddBefore = toAddBefore;
    }

    @Override
    public @IntentionName @NotNull String getText() {
        return "Add annotation text"; // TODO
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Add annotation family name";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true; // TODO ???
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
//        toAddBefore.getParent().addBefore(FregeElementFactory.createNewLine(project), toAddBefore);

        System.err.println("TEXT: " + file.getText());
        System.err.println("\nANNO: " + toAnnotate.getText());

        String type = null; // TODO fix
        List<VirtualFile> librariesUrls = new ArrayList<>();
        ProjectRootManager.getInstance(project).orderEntries().forEachLibrary(library -> {
            System.err.println(library.getName());
            VirtualFile[] urls = library.getFiles(OrderRootType.CLASSES);
            librariesUrls.addAll(new ArrayList<>(Arrays.asList(urls)));
//            System.err.println(Arrays.toString(urls));
            return true;
        });


//        URLClassLoader contextLoader = new URLClassLoader(librariesUrls.stream().map(s -> {
//            try {
//                System.err.println(s.getCanonicalPath());
//                return new URL("jar:file:/" + s.getCanonicalPath());
//            } catch (MalformedURLException e) {
//                throw new RuntimeException(e);
//            }
//        }).toArray(URL[]::new), Thread.currentThread().getContextClassLoader());

        URLClassLoader contextLoader;
        try {
            contextLoader = new URLClassLoader(new URL[]{new URL("file:/home/yourass/Desktop/proga/hse19/projects/intellij-frege/intellij-frege/lib/org/frege-lang/frege/3.24.100/frege-3.24.100.jar")}, Thread.currentThread().getContextClassLoader());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

//        try {
//            type = FregeTypeSystemUtilsJavaSupport.getTypeOfByFullText(contextLoader, file.getText(), toAnnotate.getText());
//        } catch (TypeSystemException e) {
//            System.err.println("ERROR AAA: " + e.getMessage());
//            throw new IncorrectOperationException(e);
//        }
        System.err.println("Type infered: " + type);
        toAddBefore.getParent().addBefore(FregeElementFactory.createAnnotation(project, toAnnotate.getText(), type), toAddBefore);
//        toAddBefore.getParent().addBefore(FregeElementFactory.createNewLine(project), toAddBefore);
        // TODO do we need to create FregeAnnotation, or better create FregeTopDecl instead?
    }
}
