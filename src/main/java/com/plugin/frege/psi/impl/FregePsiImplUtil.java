package com.plugin.frege.psi.impl;

import com.intellij.psi.PsiElement;
import com.plugin.frege.psi.FregeElementFactory;
import com.plugin.frege.psi.FregeFunctionName;

public class FregePsiImplUtil {

    public static String getName(FregeFunctionName functionName) {
        return functionName.getText();
    }

    public static PsiElement setName(FregeFunctionName element, String newName) {
        if (element != null) {
            FregeFunctionName functionName = FregeElementFactory.createFunctionName(element.getProject(), newName);
            element.replace(functionName);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(FregeFunctionName functionName) {
        return functionName;
    }
}
