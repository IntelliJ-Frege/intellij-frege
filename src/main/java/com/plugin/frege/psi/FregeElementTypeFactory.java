package com.plugin.frege.psi;

import com.intellij.psi.tree.IElementType;
import com.plugin.frege.stubs.types.FregeDataNativeNameElementType;
import com.plugin.frege.stubs.types.FregePackageClassNameElementType;

public class FregeElementTypeFactory {
    public static IElementType factory(String name) {
        switch (name) {
            case "DATA_NAME_NATIVE":
                return new FregeDataNativeNameElementType(name);
            case "PACKAGE_CLASS_NAME":
                return new FregePackageClassNameElementType(name);
            default:
                throw new IllegalStateException("Unknown element name: " + name);
        }
    }
}
