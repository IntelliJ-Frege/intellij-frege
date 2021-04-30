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

/**
 * Definitions:
 * <ul>
 *     <li>Scope is a {@link PsiElement} that has a list of {@link FregeDecl}. </li>
 * </ul>
 */
public class FregePsiUtilImpl {
    private static final Class<?>[] scopeElementTypes = { FregeBody.class, FregeDecls.class };

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
    public static @NotNull List<PsiElement> declsFromScopeOfElement(@NotNull PsiElement element) {
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
     * Searches for the first parent of the passed element that contains {@link FregeWhereSection}.
     * Stops when no parents anymore or {@link FregeEqualSign} was found but {@link FregeWhereSection} wasn't.
     * If {@link FregeWhereSection} was found after all, then searches for the decls of the where-expression.
     */
    public static @Nullable PsiElement findWhereDeclsInExpression(@NotNull PsiElement element) {
        while (element.getParent() != null && PsiTreeUtil.getChildOfType(element.getParent(), FregeEqualSign.class) == null) {
            element = element.getParent();
        }

        while (!(element instanceof FregeWhereSection)) {
            element = element.getNextSibling();
            if (element == null) {
                return null;
            }
        }

        // WHERE VIRTUAL_OPEN DECLS TODO maybe other patterns
        return element.getNextSibling().getNextSibling();
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
}
