package com.plugin.frege.stubs.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IStubFileElementType;
import com.plugin.frege.FregeLanguage;
import com.plugin.frege.stubs.FregeFileStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class FregeFileElementType extends IStubFileElementType<FregeFileStub> {
    public final static FregeFileElementType INSTANCE = new FregeFileElementType(FregeLanguage.INSTANCE);
    private final static int VERSION = 1; // Change the version if you want to re-index Frege

    public FregeFileElementType(FregeLanguage language) {
        super(language);
    }

    @Override
    public int getStubVersion() {
        return VERSION;
    }

    @Override
    public void serialize(@NotNull FregeFileStub stub, @NotNull StubOutputStream dataStream) {
    }

    @Override
    public @NotNull FregeFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) {
        return new FregeFileStub(null);
    }

    @Override
    public @NonNls @NotNull String getExternalId() {
        return "frege.FILE";
    }
}
