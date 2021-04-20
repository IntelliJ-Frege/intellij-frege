package com.plugin.frege.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.plugin.frege.psi.FregeNamedElement;
import org.jetbrains.annotations.NotNull;

public abstract class FregeNamedElementImpl extends ASTWrapperPsiElement implements FregeNamedElement {

    public FregeNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
