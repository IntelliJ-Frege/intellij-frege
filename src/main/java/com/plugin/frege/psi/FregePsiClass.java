package com.plugin.frege.psi;

import com.intellij.psi.PsiClass;
import com.plugin.frege.psi.impl.FregePsiUtilImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FregePsiClass extends FregeNamedElement, PsiClass, FregeElementProvideDocumentation {
    @Override
    default @NotNull List<@NotNull FregeDocumentationElement> getDocs() {
        return FregePsiUtilImpl.collectPrecedingDocs(this);
    }
}
