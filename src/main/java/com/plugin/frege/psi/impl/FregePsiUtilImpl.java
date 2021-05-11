package com.plugin.frege.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Definitions:
 * <ul>
 *     <li> Scope is a {@link PsiElement} that has a list of subprograms. </li>
 * </ul>
 */
public class FregePsiUtilImpl {
    private FregePsiUtilImpl() {}

    // TODO do-expr and case of
    private static final Class<?>[] scopeElementTypes = {
            FregeBody.class,
            FregeWhereSection.class,
            FregeLetInExpression.class
    };

    private static final Class<? extends PsiElement>[] indentSectionSubprogramsClass;

    static {
        //noinspection unchecked
        indentSectionSubprogramsClass =
                (Class<? extends PsiElement>[]) Array.newInstance(Class.class, 3);
        indentSectionSubprogramsClass[0] = FregeDecl.class; // for let expressions and where sections
        indentSectionSubprogramsClass[1] = FregeLcqual.class; // for do expressions
        indentSectionSubprogramsClass[2] = FregeAlt.class; // for case expressions
    }

    private static boolean isScope(@Nullable PsiElement element) {
        if (element == null) {
            return false;
        }

        Class<? extends PsiElement> elementClass = element.getClass();
        return Arrays.stream(scopeElementTypes)
                .anyMatch(type -> type.isAssignableFrom(elementClass));
    }

    private static @NotNull List<PsiElement> subprogramsFromScope(@Nullable PsiElement scope) {
        if (!isScope(scope)) {
            return List.of();
        }

        if (scope instanceof FregeBody) {
            return ((FregeBody) scope).getTopDeclList().stream()
                    .map(FregeTopDecl::getDecl)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else if (scope instanceof FregeWhereSection) {
            FregeIndentSection indentSection = ((FregeWhereSection) scope).getIndentSection();
            return PsiTreeUtil.getChildrenOfAnyType(indentSection, indentSectionSubprogramsClass);
        } else if (scope instanceof FregeLetInExpression) {
            FregeIndentSection indentSection = ((FregeLetInExpression) scope).getLetExpression().getIndentSection();
            return PsiTreeUtil.getChildrenOfAnyType(indentSection, indentSectionSubprogramsClass);
        } else {
            throw new RuntimeException("Cannot get subprograms.");
        }
    }

    /**
     * Finds the first parent of the passed element that presents a scope.
     * Returns the passed element, if it is a scope.
     * Returns {@code null} if there is no a scope for the element.
     */
    public static @Nullable PsiElement scopeOfElement(@NotNull PsiElement element) {
        while (element != null && !isScope(element)) {
            element = element.getParent();
        }

        return element;
    }

    /**
     * Finds a scope of the passed element, gets a list of {@link PsiElement}
     * and applies the passed getter for each of {@link PsiElement}.
     * Also filters {@code nulls} in the resulting list.
     */
    public static @NotNull <T extends PsiElement> List<T> subprogramsFromScopeOfElement(@NotNull PsiElement element,
                                                                                        @NotNull Function<PsiElement, T> getter) {
        PsiElement scope = scopeOfElement(element);
        if (!isScope(scope)) {
            return List.of();
        }

        return subprogramsFromScope(scope).stream()
                .map(getter)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Finds a scope of the passed element and gets a list of {@link PsiElement}.
     */
    public static @NotNull List<PsiElement> subprogramsFromScopeOfElement(@NotNull PsiElement element) {
        return subprogramsFromScopeOfElement(element, Function.identity());
    }

    /**
     * Returns a predicate, accepting only {@link PsiElement}
     * for which {@link PsiElement#getText()} equals the passed text.
     */
    public static @NotNull Predicate<PsiElement> keepWithText(@NotNull String text) {
        return elem -> elem != null && text.equals(elem.getText());
    }

    /**
     * Searches for the first binding and if it contains {@link FregeWhereSection}, returns it.
     * Otherwise {@code null} is returned.
     */
    public static @Nullable FregeWhereSection findWhereInExpression(@NotNull PsiElement element) {
        FregeBinding binding = parentBinding(element);
        if (findElementsWithinElementStream(binding, (elem -> elem.equals(element))).noneMatch(x -> true)) {
            return null;
        }
        return (FregeWhereSection) findElementsWithinElementStream(binding, (elem -> elem instanceof FregeWhereSection))
                .findFirst().orElse(null);
    }

    /**
     * Checks if the passed scope is global.
     * @throws IllegalArgumentException if the passed element is not a scope.
     */
    public static boolean isScopeGlobal(@Nullable PsiElement scope) {
        if (!isScope(scope)) {
            throw new IllegalArgumentException("The passed element is not a scope.");
        }

        return scope instanceof FregeBody;
    }

    /**
     * Checks if the passed element is in the global scope.
     * It means that it is one of {@link FregeTopDecl}.
     */
    public static boolean isInGlobalScope(@NotNull PsiElement element) {
        return isScopeGlobal(scopeOfElement(element));
    }

    /**
     * Returns a global scope for the passed element or {@code null} if there is no scope.
     */
    public static @Nullable PsiElement globalScopeOfElement(@Nullable PsiElement element) {
        if (element == null) {
            return null;
        } else if (isScope(element) && isScopeGlobal(element)) {
            return element;
        } else {
            return globalScopeOfElement(element.getParent());
        }
    }

    /**
     * Searches for elements within the scope of the passed element that match the passed predicate.
     */
    public static @NotNull List<PsiElement> findElementsWithinScope(@NotNull PsiElement element,
                                                                    @NotNull Predicate<PsiElement> predicate) {
        List<PsiElement> subprograms = subprogramsFromScopeOfElement(element);
        return subprograms.stream()
                .flatMap(subprogram -> findElementsWithinElementStream(subprogram, predicate))
                .collect(Collectors.toList());
    }

    private static @NotNull Stream<PsiElement> findElementsWithinElementStream(@Nullable PsiElement element,
                                                                               @NotNull Predicate<PsiElement> predicate) {
        if (element == null) {
            return Stream.of();
        }
        if (predicate.test(element)) {
            return Stream.of(element);
        }
        if (isScope(element)) {
            return Stream.of();
        }

        return Arrays.stream(element.getChildren())
                .flatMap(elem -> findElementsWithinElementStream(elem, predicate));
    }

    /**
     * Searches for the elements matched the passed predicate in the children of the passed element within its scope.
     */
    public static @NotNull List<PsiElement> findElementsWithinElement(@Nullable PsiElement element,
                                                                      @NotNull Predicate<PsiElement> predicate) {
        return findElementsWithinElementStream(element, predicate).collect(Collectors.toList());
    }

    /**
     * Returns the first parent which is {@link FregeBinding}.
     */
    public static @Nullable FregeBinding parentBinding(@Nullable PsiElement element) {
        return PsiTreeUtil.getParentOfType(element, FregeBinding.class);
    }

    public static @NotNull List<FregeDataDcl> findAvailableDataDecls(@NotNull PsiElement element) {
        PsiElement globalScope = globalScopeOfElement(element);
        if (globalScope == null) {
            return List.of();
        }
        if (!(globalScope instanceof FregeBody)) {
            throw new IllegalStateException("Global scope must be Frege body.");
        }
        FregeBody body = (FregeBody) globalScope;

        return body.getTopDeclList().stream()
                .map(FregeTopDecl::getDataDcl)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * @return the module name of psi, if presented, or null otherwise
     */
    public static @Nullable String getModuleName(PsiElement psi) {
        while (psi != null && !(psi instanceof FregeProgram)) {
            psi = psi.getParent();
        }
        if (psi == null)
            throw new IllegalArgumentException("PsiElement is not a part of Frege program");
        FregePackageName packageName = ((FregeProgram) psi).getPackageName();
        if (packageName == null)
            return null;
        return packageName.getText();
    }

    /**
     * Checks if the passed element is a leaf in the PSI tree.
     */
    public static boolean isLeaf(@NotNull PsiElement element) {
        return element.getFirstChild() == null;
    }

    /**
     * Finds all imports the element can access to. It doesn't return standard imports like frege.Prelude.
     */
    public static @NotNull List<@NotNull FregeImportDcl> findImportsForElement(@NotNull PsiElement element) {
        FregeBody body = PsiTreeUtil.getParentOfType(element, FregeBody.class);
        if (body == null) {
            return List.of();
        }
        return body.getTopDeclList().stream()
                .map(FregeTopDecl::getImportDcl)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Gets the last word after the last '.' in the passed qualified name.
     */
    public static @NotNull String nameFromQualifiedName(@NotNull String qualifiedName) {
        if (!qualifiedName.contains(".")) {
            return qualifiedName;
        }
        return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
    }

    /**
     * Gets the prefix before the last '.' in the passed qualified name.
     */
    public static @NotNull String qualifierFromQualifiedName(@NotNull String qualifiedName) {
        if (!qualifiedName.contains(".")) {
            return "";
        }
        return qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
    }
}
