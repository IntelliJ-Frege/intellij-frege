package com.plugin.frege.psi;

import com.intellij.psi.tree.IElementType;
import com.plugin.frege.FregeLanguage;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class FregeTokenType extends IElementType {

    public FregeTokenType(@NotNull @NonNls String debugName) {
        super(debugName, FregeLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "FregeTokenType." + super.toString();
    }
}
