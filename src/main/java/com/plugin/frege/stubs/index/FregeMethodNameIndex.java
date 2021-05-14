package com.plugin.frege.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.plugin.frege.psi.FregePsiMethod;
import org.jetbrains.annotations.NotNull;

public class FregeMethodNameIndex extends StringStubIndexExtension<FregePsiMethod> {
    private static final StubIndexKey<String, FregePsiMethod> KEY =
            StubIndexKey.createIndexKey("com.plugin.frege.stubs.index.FregeMethodNameIndex");
    private static final FregeMethodNameIndex INSTANCE = new FregeMethodNameIndex();

    private FregeMethodNameIndex() { }

    public static FregeMethodNameIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public @NotNull StubIndexKey<String, FregePsiMethod> getKey() {
        return KEY;
    }
}
