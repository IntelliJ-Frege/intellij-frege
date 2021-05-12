package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.plugin.frege.psi.FregeBody;
import com.plugin.frege.psi.FregeScopeElement;
import com.plugin.frege.psi.FregeTopDecl;
import com.plugin.frege.psi.impl.FregeCompositeElementImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FregeBodyMixin extends FregeCompositeElementImpl implements FregeScopeElement {
    public FregeBodyMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull List<PsiElement> getSubprogramsFromScope() {
        if (!(this instanceof FregeBody)) {
            return List.of();
        }
        return ((FregeBody) this).getTopDeclList().stream()
                .map(FregeTopDecl::getDecl)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public PsiReference getReference() {
        return null;
    }
}
