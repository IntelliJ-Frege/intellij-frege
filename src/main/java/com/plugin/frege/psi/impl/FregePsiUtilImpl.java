package com.plugin.frege.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 *     <li> Scope is a {@link PsiElement} that has a list of {@link FregeDecl}. </li>
 * </ul>
 */
public class FregePsiUtilImpl {
    private FregePsiUtilImpl() {}

    // TODO do-expr and lambdas
    private static final Class<?>[] scopeElementTypes = {
            FregeBody.class,
            FregeDecls.class,
            FregeLetEx.class
    };

    private static boolean isScope(@Nullable PsiElement element) {
        if (element == null) {
            return false;
        }

        Class<? extends PsiElement> elementClass = element.getClass();
        return Arrays.stream(scopeElementTypes)
                .anyMatch(type -> type.isAssignableFrom(elementClass));
    }

    private static @NotNull List<FregeDecl> declsFromScope(@Nullable PsiElement scope) {
        if (!isScope(scope)) {
            return List.of();
        }

        if (scope instanceof FregeBody) {
            return ((FregeBody) scope).getTopDeclList().stream()
                    .map(FregeTopDecl::getDecl)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else if (scope instanceof FregeDecls) {
            return ((FregeDecls) scope).getDeclList();
        } else if (scope instanceof FregeLetEx) {
            FregeDecls decls = ((FregeLetEx) scope).getDecls();
            if (decls == null) {
                return List.of();
            }
            return decls.getDeclList();
        } else {
            throw new RuntimeException("Cannot get decls.");
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
     * Finds a scope of the passed element, gets a list of {@link FregeDecl}
     * and applies the passed getter for each of {@link FregeDecl}.
     * Also filters {@code nulls} in the resulting list.
     */
    public static @NotNull <T extends PsiElement> List<T> declsFromScopeOfElement(@NotNull PsiElement element,
                                                                    @NotNull Function<FregeDecl, T> getter) {
        PsiElement scope = scopeOfElement(element);
        if (!isScope(scope)) {
            return List.of();
        }

        return declsFromScope(scope).stream()
                .map(getter)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Finds a scope of the passed element and gets a list of {@link FregeDecl}.
     */
    public static @NotNull List<FregeDecl> declsFromScopeOfElement(@NotNull PsiElement element) {
        return declsFromScopeOfElement(element, (decl -> decl));
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
        List<FregeDecl> decls = declsFromScopeOfElement(element);
        return decls.stream()
                .flatMap(decl -> findElementsWithinElementStream(decl, predicate))
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
}
