package com.plugin.frege.psi;

import com.intellij.psi.PsiClass;

public interface FregePsiClass extends FregeNamedElement, PsiClass, FregeDocumentableElement {
    /**
     * Returns `true` if this element can be used as a resolved reference.
     */
    default boolean canBeReferenced() {
        return true;
    }
}
