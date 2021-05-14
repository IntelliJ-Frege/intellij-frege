package com.plugin.frege.stubs.types;

import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.psi.impl.FregeDataNameNativeImpl;
import com.plugin.frege.stubs.FregeClassStub;
import org.jetbrains.annotations.NotNull;

public class FregeDataNativeNameElementType extends FregeClassElementType {
    public FregeDataNativeNameElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public FregePsiClass createPsi(@NotNull FregeClassStub stub) {
        return new FregeDataNameNativeImpl(stub, this);
    }

    @Override
    public @NotNull String getExternalId() {
        return super.getExternalId() + ".DATA_NATIVE";
    }
}
