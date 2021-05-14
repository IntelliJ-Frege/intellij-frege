package com.plugin.frege.stubs.types;

import com.intellij.lang.Language;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.plugin.frege.FregeLanguage;
import com.plugin.frege.psi.FregeCompositeElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class FregeStubElementType<StubT extends StubElement<?>, PsiT extends FregeCompositeElement>
        extends IStubElementType<StubT, PsiT> {

    public FregeStubElementType(@NotNull @NonNls String debugName) {
        super(debugName, FregeLanguage.INSTANCE);
    }

    @Override
    public @NotNull Language getLanguage() {
        return FregeLanguage.INSTANCE;
    }

    @Override
    public @NotNull String getExternalId() {
        return "frege." + super.toString();
    }
}
