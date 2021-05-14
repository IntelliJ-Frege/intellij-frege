package com.plugin.frege.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import com.plugin.frege.psi.FregePsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO keep more data
public class FregeClassStub extends FregeNamedStub<FregePsiClass> {
    public FregeClassStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable String name) {
        super(parent, elementType, name);
    }

    public FregeClassStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable StringRef name) {
        super(parent, elementType, name);
    }
}
