package com.plugin.frege.stubs.types;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.plugin.frege.psi.FregeClassDecl;
import com.plugin.frege.psi.FregePsiMethod;
import com.plugin.frege.psi.impl.FregeAnnotationItemImpl;
import com.plugin.frege.stubs.FregeMethodStub;
import org.jetbrains.annotations.NotNull;

public class FregeAnnotationItemElementType extends FregeMethodElementType {
    public FregeAnnotationItemElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public @NotNull String getExternalId() {
        return super.getExternalId() + ".ANNOTATION_ITEM";
    }

    @Override
    public FregePsiMethod createPsi(@NotNull FregeMethodStub stub) {
        return new FregeAnnotationItemImpl(stub, this);
    }

    // a workaround in order not to consider all annotations as methods
    @Override
    public boolean shouldCreateStub(ASTNode node) {
        PsiElement element = node.getPsi();
        if (!(element instanceof FregeAnnotationItemImpl)) {
            return true;
        }
        FregeAnnotationItemImpl annotation = (FregeAnnotationItemImpl) element;
        return annotation.getContainingClass() instanceof FregeClassDecl;
    }
}
