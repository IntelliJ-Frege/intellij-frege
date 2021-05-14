package com.plugin.frege.stubs.types;

import com.intellij.psi.stubs.*;
import com.plugin.frege.psi.FregeNamedElement;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class FregeNamedStubElementType<StubT extends NamedStubBase<?>, PsiT extends FregeNamedElement> extends FregeStubElementType<StubT, PsiT> {
    public FregeNamedStubElementType(@NotNull String debugName) {
        super(debugName);
    }

    protected abstract @NotNull StubIndexKey<String, PsiT> getKey();

    @Override
    public void serialize(@NotNull StubT stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @Override
    public void indexStub(@NotNull StubT stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(getKey(), name);
        }
    }
}
