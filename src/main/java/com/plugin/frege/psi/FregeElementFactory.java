package com.plugin.frege.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.FregeLanguage;

public class FregeElementFactory {
    private static final String fakeModuleName = "Dummy";
    private static final String fakeFileName = fakeModuleName + ".fr";
    private static final String fakeProgram = "module " + fakeModuleName + " where\n";

    private static FregeFile createFile(Project project, String text) {
        return (FregeFile) PsiFileFactory.getInstance(project).createFileFromText(fakeFileName, FregeLanguage.INSTANCE, text);
    }

    private static <E extends PsiElement> E createElement(Project project, String text, Class<E> elementClass) {
        FregeFile file = createFile(project, text);
        return PsiTreeUtil.findChildrenOfType(file, elementClass).stream()
                .findFirst().orElseThrow(() ->
                        new IllegalStateException("Cannot create a function with a custom name."));
    }

    public static FregeFunctionName createFunctionName(Project project, String name) {
        String fakeFunction = fakeProgram + name + " = undefined";
        return createElement(project, fakeFunction, FregeFunctionName.class);
    }
}
