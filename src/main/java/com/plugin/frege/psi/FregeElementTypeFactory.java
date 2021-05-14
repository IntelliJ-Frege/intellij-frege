package com.plugin.frege.psi;

import com.intellij.psi.tree.IElementType;
import com.plugin.frege.stubs.types.FregeClassElementType;

public class FregeElementTypeFactory {
    public static IElementType factory(String name) {
        if (name.equals("DATA_NAME_NATIVE")) {
            return new FregeClassElementType(name);
        } else {
            throw new IllegalStateException("Unknown element name: " + name);
        }
    }
}
