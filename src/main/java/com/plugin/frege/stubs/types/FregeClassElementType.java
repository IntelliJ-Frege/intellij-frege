package com.plugin.frege.stubs.types;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.plugin.frege.psi.FregePsiClass;
import com.plugin.frege.psi.impl.FregeDataNameNativeImpl;
import com.plugin.frege.stubs.FregeClassStub;
import com.plugin.frege.stubs.index.FregeClassNameIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;

public class FregeClassElementType extends FregeNamedStubElementType<FregeClassStub, FregePsiClass> {
    public FregeClassElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    protected @NotNull StubIndexKey<String, FregePsiClass> getKey() {
        return FregeClassNameIndex.getInstance().getKey();
    }

    @Override
    public FregePsiClass createPsi(@NotNull FregeClassStub stub) {
        return FregeClassType.values()[stub.getTypeOrder()].create(stub, this);
    }

    @Override
    public @NotNull FregeClassStub createStub(@NotNull FregePsiClass psi, StubElement<?> parentStub) {
        return new FregeClassStub(parentStub, this, psi.getQualifiedName(), getType(psi));
    }

    @Override
    public void serialize(@NotNull FregeClassStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeShort(stub.getTypeOrder());
    }

    @Override
    public @NotNull FregeClassStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        int type = dataStream.readShort();
        return new FregeClassStub(parentStub, this, name, type);
    }

    @Override
    public @NotNull String getExternalId() {
        return "frege.CLASS";
    }

    private int getType(FregePsiClass psiClass) {
        return FregeClassType.getTypeByClass(psiClass.getClass());
    }

    private enum FregeClassType {
        @SuppressWarnings("unused")
        DATA_NATIVE {
            public FregePsiClass create(FregeClassStub stub, IStubElementType<?, ?> stubType) {
                return new FregeDataNameNativeImpl(stub, stubType);
            }
            public Class<?> getCorrespondingClass() {
                return FregeDataNameNativeImpl.class;
            }
        }; // TODO add more classes

        protected abstract Class<?> getCorrespondingClass();

        public abstract FregePsiClass create(FregeClassStub stub, IStubElementType<?, ?> stubType);

        public static int getTypeByClass(Class<?> clazz) {
            return Arrays.stream(FregeClassType.values())
                    .filter(value -> value.getCorrespondingClass() == clazz)
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Such class is not supported."))
                    .ordinal();
        }
    }
}
