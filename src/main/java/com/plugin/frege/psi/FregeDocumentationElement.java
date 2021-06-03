package com.plugin.frege.psi;

import org.jetbrains.annotations.NotNull;

public interface FregeDocumentationElement extends FregeCompositeElement {
    @NotNull String getDocumentationText();
}
