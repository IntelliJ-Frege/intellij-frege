package com.plugin.frege.stubs.types;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.plugin.frege.psi.FregeClassDcl;
import com.plugin.frege.psi.FregePsiMethod;
import com.plugin.frege.psi.impl.FregeAnnotationImpl;
import com.plugin.frege.stubs.FregeMethodStub;
import org.jetbrains.annotations.NotNull;

public class FregeAnnotationElementType extends FregeMethodElementType {
    public FregeAnnotationElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public @NotNull String getExternalId() {
        return super.getExternalId() + ".ANNOTATION";
    }

    @Override
    public FregePsiMethod createPsi(@NotNull FregeMethodStub stub) {
        return new FregeAnnotationImpl(stub, this);
    }

    // a workaround in order not to consider all annotations as methods
    @Override
    public boolean shouldCreateStub(ASTNode node) {
        PsiElement element = node.getPsi();
        if (!(element instanceof FregeAnnotationImpl)) {
            return true;
        }
        FregeAnnotationImpl annotation = (FregeAnnotationImpl) element;
        return annotation.getContainingClass() instanceof FregeClassDcl;
    }
}
