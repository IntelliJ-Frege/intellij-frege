package com.plugin.frege.psi.mixin;

import com.intellij.lang.ASTNode;
import com.plugin.frege.psi.impl.FregeCompositeElementImpl;
import com.plugin.frege.resolve.FregeQVaridReference;
import org.jetbrains.annotations.NotNull;

public class FregeQVaridMixin extends FregeCompositeElementImpl {
    public FregeQVaridMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public FregeQVaridReference getReference() {
        return new FregeQVaridReference(this);
    }
}
