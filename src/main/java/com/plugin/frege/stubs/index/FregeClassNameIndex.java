package com.plugin.frege.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.plugin.frege.psi.FregePsiClass;
import org.jetbrains.annotations.NotNull;

public class FregeClassNameIndex extends StringStubIndexExtension<FregePsiClass> {
    private static final StubIndexKey<String, FregePsiClass> KEY =
            StubIndexKey.createIndexKey("com.plugin.frege.stubs.index.FregeClassNameIndex");
    private static final FregeClassNameIndex INSTANCE = new FregeClassNameIndex();

    private FregeClassNameIndex() { }

    public static FregeClassNameIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public @NotNull StubIndexKey<String, FregePsiClass> getKey() {
        return KEY;
    }
}
