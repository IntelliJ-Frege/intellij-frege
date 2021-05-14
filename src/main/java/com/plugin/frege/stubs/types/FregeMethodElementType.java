package com.plugin.frege.stubs.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.util.io.StringRef;
import com.plugin.frege.psi.FregePsiMethod;
import com.plugin.frege.stubs.FregeMethodStub;
import com.plugin.frege.stubs.index.FregeMethodNameIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class FregeMethodElementType extends FregeNamedStubElementType<FregeMethodStub, FregePsiMethod> {
    public FregeMethodElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    protected @NotNull StubIndexKey<String, FregePsiMethod> getKey() {
        return FregeMethodNameIndex.getInstance().getKey();
    }

    @Override
    public @NotNull FregeMethodStub createStub(@NotNull FregePsiMethod psi, StubElement<?> parentStub) {
        return new FregeMethodStub(parentStub, this, psi.getName());
    }

    @Override
    public @NotNull FregeMethodStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        return new FregeMethodStub(parentStub, this, name);
    }

    @Override
    public @NotNull String getExternalId() {
        return "frege.METHOD";
    }
}
