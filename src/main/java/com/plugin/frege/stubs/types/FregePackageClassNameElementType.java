package com.plugin.frege.stubs.types;

import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.psi.impl.FregePackageClassNameImpl;
import com.plugin.frege.stubs.FregeClassStub;
import org.jetbrains.annotations.NotNull;

public class FregePackageClassNameElementType extends FregeClassElementType{
    public FregePackageClassNameElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public @NotNull String getExternalId() {
        return super.getExternalId() + ".PACKAGE_CLASS_NAME";
    }

    @Override
    public FregePsiClass createPsi(@NotNull FregeClassStub stub) {
        return new FregePackageClassNameImpl(stub, this);
    }
}
