package com.plugin.frege.stubs.types;

import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.psi.impl.FregeClassDclImpl;
import com.plugin.frege.stubs.FregeClassStub;
import org.jetbrains.annotations.NotNull;

public class FregeClassDclElementType extends FregeClassElementType {
    public FregeClassDclElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public @NotNull String getExternalId() {
        return super.getExternalId() + ".CLASS_DCL";
    }

    @Override
    public FregePsiClass createPsi(@NotNull FregeClassStub stub) {
        return new FregeClassDclImpl(stub, this);
    }
}
