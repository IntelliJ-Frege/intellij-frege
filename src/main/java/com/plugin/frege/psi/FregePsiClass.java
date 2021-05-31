package com.plugin.frege.psi;

import com.intellij.psi.PsiClass;

public interface FregePsiClass extends FregeNamedElement, PsiClass {
    /**
     * Indicates that search of members of this class without class-name-qualifier is allowed.
     */
    boolean notQualifiedSearchAllowed();
}
