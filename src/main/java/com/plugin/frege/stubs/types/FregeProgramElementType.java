package com.plugin.frege.stubs.types;

import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.psi.impl.FregeProgramImpl;
import com.plugin.frege.stubs.FregeClassStub;
import org.jetbrains.annotations.NotNull;

public class FregeProgramElementType extends FregeClassElementType{
    public FregeProgramElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public @NotNull String getExternalId() {
        return super.getExternalId() + ".PROGRAM";
    }

    @Override
    public FregePsiClass createPsi(@NotNull FregeClassStub stub) {
        return new FregeProgramImpl(stub, this);
    }
}
