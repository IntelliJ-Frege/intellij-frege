package com.plugin.frege.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import com.plugin.frege.psi.FregeNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FregeNamedStub<T extends FregeNamedElement> extends NamedStubBase<T> {
    public FregeNamedStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable StringRef name) {
        super(parent, elementType, name);
    }

    public FregeNamedStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable String name) {
        super(parent, elementType, name);
    }
}
