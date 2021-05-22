package com.plugin.frege.stubs.types;

import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.psi.impl.FregeDataDclNativeImpl;
import com.plugin.frege.stubs.FregeClassStub;
import org.jetbrains.annotations.NotNull;

public class FregeDataDclNativeElementType extends FregeClassElementType {
    public FregeDataDclNativeElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public FregePsiClass createPsi(@NotNull FregeClassStub stub) {
        return new FregeDataDclNativeImpl(stub, this);
    }

    @Override
    public @NotNull String getExternalId() {
        return super.getExternalId() + ".DATA_NATIVE";
    }
}
