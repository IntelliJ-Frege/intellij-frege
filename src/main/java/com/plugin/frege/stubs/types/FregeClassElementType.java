package com.plugin.frege.stubs.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.util.io.StringRef;
import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.stubs.FregeClassStub;
import com.plugin.frege.stubs.index.FregeClassNameIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class FregeClassElementType extends FregeNamedStubElementType<FregeClassStub, FregePsiClass> {
    public FregeClassElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    protected @NotNull StubIndexKey<String, FregePsiClass> getKey() {
        return FregeClassNameIndex.getInstance().getKey();
    }

    @Override
    public @NotNull FregeClassStub createStub(@NotNull FregePsiClass psi, StubElement<?> parentStub) {
        return new FregeClassStub(parentStub, this, psi.getQualifiedName());
    }

    @Override
    public @NotNull FregeClassStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        return new FregeClassStub(parentStub, this, name);
    }

    @Override
    public @NotNull String getExternalId() {
        return "frege.CLASS";
    }
}
