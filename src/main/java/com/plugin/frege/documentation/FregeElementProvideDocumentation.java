package com.plugin.frege.documentation;

import com.plugin.frege.psi.FregeCompositeElement;
import com.plugin.frege.psi.FregeDocumentationElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FregeElementProvideDocumentation extends FregeCompositeElement {
    @NotNull List<@NotNull FregeDocumentationElement> getDocs();
}
