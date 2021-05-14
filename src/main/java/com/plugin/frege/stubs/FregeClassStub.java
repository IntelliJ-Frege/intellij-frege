package com.plugin.frege.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import com.plugin.frege.psi.FregePsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO keep more data
public class FregeClassStub extends NamedStubBase<FregePsiClass> {
    /**
     * In order to distinct different classes (native data, module, etc)
     */
    private final int typeOrder;

    public FregeClassStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable String name, int typeOrder) {
        super(parent, elementType, name);
        this.typeOrder = typeOrder;
    }

    public FregeClassStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable StringRef name, int typeOrder) {
        super(parent, elementType, name);
        this.typeOrder = typeOrder;
    }

    @Override
    public @Nullable String getName() {
        return super.getName();
    }

    public int getTypeOrder() {
        return typeOrder;
    }
}
