package com.plugin.frege.stubs.types;

import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.psi.impl.FregeClassDeclImpl;
import com.plugin.frege.stubs.FregeClassStub;
import org.jetbrains.annotations.NotNull;

public class FregeClassDeclElementType extends FregeClassElementType {
    public FregeClassDeclElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public @NotNull String getExternalId() {
        return super.getExternalId() + ".CLASS_DECL";
    }

    @Override
    public FregePsiClass createPsi(@NotNull FregeClassStub stub) {
        return new FregeClassDeclImpl(stub, this);
    }
}
