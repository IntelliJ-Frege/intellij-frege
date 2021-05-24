package com.plugin.frege.stubs.types;

import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.psi.impl.FregeNativeDataDeclImpl;
import com.plugin.frege.stubs.FregeClassStub;
import org.jetbrains.annotations.NotNull;

public class FregeNativeDataDeclElementType extends FregeClassElementType {
    public FregeNativeDataDeclElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public FregePsiClass createPsi(@NotNull FregeClassStub stub) {
        return new FregeNativeDataDeclImpl(stub, this);
    }

    @Override
    public @NotNull String getExternalId() {
        return super.getExternalId() + ".NATIVE_DATA";
    }
}
