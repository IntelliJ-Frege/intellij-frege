package com.plugin.frege.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.plugin.frege.psi.FregeNamedElement;
import org.jetbrains.annotations.NotNull;

public abstract class FregeNamedStubBasedPsiElementBase<T extends StubElement<?>> extends StubBasedPsiElementBase<T> implements FregeNamedElement {
    public FregeNamedStubBasedPsiElementBase(@NotNull T stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public FregeNamedStubBasedPsiElementBase(@NotNull ASTNode node) {
        super(node);
    }
}
