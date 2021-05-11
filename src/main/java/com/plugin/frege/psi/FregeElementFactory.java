package com.plugin.frege.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.FregeLanguage;
import org.jetbrains.annotations.NotNull;

public class FregeElementFactory {
    private static final String fakeModuleName = "Dummy";
    private static final String fakeFileName = fakeModuleName + ".fr";
    private static final String fakeProgram = "module " + fakeModuleName + " where\n";

    private static @NotNull FregeFile createFile(Project project, String text) {
        return (FregeFile) PsiFileFactory.getInstance(project).createFileFromText(fakeFileName, FregeLanguage.INSTANCE, text);
    }

    private static <E extends PsiElement> @NotNull E createElement(Project project, String text, Class<E> elementClass) {
        FregeFile file = createFile(project, text);
        return PsiTreeUtil.findChildrenOfType(file, elementClass).stream()
                .reduce((fir, sec) -> sec).orElseThrow(() ->
                        new IllegalStateException("Cannot create an element with a custom name."));
    }

    public static @NotNull FregeFunctionName createFunctionName(Project project, String name) {
        String fakeFunction = fakeProgram + name + " = undefined";
        return createElement(project, fakeFunction, FregeFunctionName.class);
    }

    public static @NotNull FregeParam createParam(Project project, String name) {
        String fakeParam = fakeProgram + "function " + name + " = undefined";
        return createElement(project, fakeParam, FregeParam.class);
    }

    public static @NotNull FregeQVarId createVarId(Project project, String name) {
        String fakeVarId = fakeProgram + "function = " + name;
        return createElement(project, fakeVarId, FregeQVarId.class);
    }

    public static @NotNull FregeNativeName createNativeName(Project project, String name) {
        String fakeNativeName = fakeProgram + "pure native Dummy = " + name;
        return createElement(project, fakeNativeName, FregeNativeName.class);
    }

    public static @NotNull FregeDataNameNative createDataNameNative(Project project, String name) {
        String fakeDataName = fakeProgram + "data " + name + " = DummyConstructor";
        return createElement(project, fakeDataName, FregeDataNameNative.class);
    }

    public static @NotNull FregeDataNameUsage createDataNameUsage(Project project, String name) {
        String fakeDataNameUsage = fakeProgram + "func :: " + name;
        return createElement(project, fakeDataNameUsage, FregeDataNameUsage.class);
    }
}
