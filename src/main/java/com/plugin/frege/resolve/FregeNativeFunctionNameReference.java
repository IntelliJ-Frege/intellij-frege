package com.plugin.frege.resolve;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.plugin.frege.psi.impl.FregePsiClassUtilImpl.getClassesByQualifiedName;
import static com.plugin.frege.psi.impl.FregePsiClassUtilImpl.getMethodsAndFieldsByName;

public class FregeNativeFunctionNameReference extends FregeReferenceBase {
    public FregeNativeFunctionNameReference(@NotNull PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
    }

    // TODO take into account: signatures
    @Override
    protected List<PsiElement> resolveInner(boolean incompleteCode) {
        FregeNativeFun nativeFunction = PsiTreeUtil.getParentOfType(element, FregeNativeFun.class);
        if (nativeFunction == null) {
            return List.of();
        }

        List<String> nativeNames;
        String methodName;
        PsiElement nativeNameFromJavaItem = getNativeNameFromJavaItem(nativeFunction.getJavaItem());
        if (nativeNameFromJavaItem != null) {
            String nativeNameString = nativeNameFromJavaItem.getText();
            if (nativeNameString.contains(".")) {
                nativeNames = List.of(nativeNameString.substring(0, nativeNameString.lastIndexOf('.')));
                methodName = nativeNameString.substring(nativeNameString.lastIndexOf('.') + 1);
            } else {
                nativeNames = List.of();
                methodName = nativeNameString;
            }
        } else {
            List<FregeSigma> sigmas = nativeFunction.getSigmaList();
            if (sigmas.isEmpty()) {
                return List.of();
            }

            FregeSigma sigma = sigmas.get(0);
            FregeDataNameUsage dataNameUsage = PsiTreeUtil.findChildOfType(sigma, FregeDataNameUsage.class);
            nativeNames = getNativeNamesFromDataNameUsage(dataNameUsage, incompleteCode);
            methodName = element.getText();
        }

        Project project = element.getProject();
        return nativeNames.stream()
                .flatMap(name -> getClassesByQualifiedName(project, name).stream())
                .filter(Objects::nonNull)
                .flatMap(psiClass -> getMethodsAndFieldsByName(psiClass, methodName).stream())
                .collect(Collectors.toList());
    }

    private @NotNull List<String> getNativeNamesFromDataNameUsage(@Nullable FregeDataNameUsage dataNameUsage,
                                                                      boolean incompleteCode) {
        if (dataNameUsage == null) {
            return List.of();
        }
        return new FregeDataNameUsageReference(dataNameUsage).resolveInner(incompleteCode).stream()
                .map(this::getNativeNameFromData)
                .filter(Objects::nonNull)
                .map(PsiElement::getText)
                .collect(Collectors.toList());
    }

    private @Nullable PsiElement getNativeNameFromJavaItem(@Nullable FregeJavaItem javaItem) {
        if (javaItem == null) {
            return null;
        }
        return javaItem.getNativeName();
    }

    private @Nullable FregeNativeName getNativeNameFromData(@NotNull PsiElement dataName) {
        if (!(dataName instanceof FregeDataNameNative)) {
            return null;
        }
        FregeDataDclNative dataNative = PsiTreeUtil.getParentOfType(dataName, FregeDataDclNative.class);
        if (dataNative == null) {
            return null;
        }
        return PsiTreeUtil.getChildOfType(dataNative, FregeNativeName.class);
    }
}
