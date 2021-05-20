package com.plugin.frege.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.psi.FregeBinding;
import com.plugin.frege.psi.FregeFile;
import com.plugin.frege.psi.FregeFunctionName;
import com.plugin.frege.psi.FregeTopDecl;
import com.plugin.frege.psi.impl.FregeFunctionNameImpl;
import com.plugin.frege.psi.impl.FregePsiUtilImpl;
import com.plugin.frege.quickfix.FregeAddAnnotationQuickFix;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.plugin.frege.psi.impl.FregePsiUtilImpl.findElementsWithinScope;

public class FregeAnnotatorWithType implements Annotator {

    // TODO move to Utils (?)
    // TODO copypaste of FregeFunctionBindingToAnnotationLineMarker, which will be deleted (?)
    private String getFullFunctionDefinitionsText(@NotNull PsiElement element, @NotNull List<PsiElement> sameFunctionsInScope) {
//        return Arrays.stream(functionNameReference.multiResolve(false))
//                .map(ResolveResult::getElement)
//                .filter(FregeFunctionNameImpl.class::isInstance)
//                .map(FregeFunctionNameImpl.class::cast)
//                .filter(FregeFunctionNameMixin::isFunctionBinding)
//                .map(PsiElement::getText)
//                .collect(Collectors.joining("\n"));
//                .collect(Collectors.toList());
        return sameFunctionsInScope.stream().
                map(FregePsiUtilImpl::getParentBinding).filter(Objects::nonNull).map(PsiElement::getText).collect(Collectors.joining("\n"));
    }

    private @NotNull FregeBinding getFirstBinding(@NotNull PsiElement element, @NotNull List<PsiElement> sameFunctionsInScope) {
        return Objects.requireNonNull(sameFunctionsInScope.stream().
                map(FregePsiUtilImpl::getParentBinding).filter(Objects::nonNull).findFirst().
                orElse(FregePsiUtilImpl.getParentBinding(element)) // workaround TODO
        );
    }

    private boolean hasAnnotation(@NotNull PsiElement element, @NotNull List<PsiElement> sameFunctionsInScope) {
        return sameFunctionsInScope.stream()
                .map(FregeFunctionNameImpl.class::cast)
                .anyMatch(FregeFunctionNameImpl::isFunctionAnnotation);
    }

    @NotNull
    private List<PsiElement> getSameFunctionInScope(@NotNull PsiElement element) {
        String referenceText = element.getText();
        return findElementsWithinScope(element,
                elem -> elem instanceof FregeFunctionName && elem.getText().equals(referenceText));
    }

//    // TODO improve?
//    private boolean isFirstBinding(@NotNull PsiElement element) {
//        return getFirstBinding(element) == FregePsiUtilImpl.getParentBinding(element);
//    }


    private String getTextOfCurrentModule(@NotNull PsiElement element) {
        return Objects.requireNonNull(PsiTreeUtil.getParentOfType(element, FregeFile.class)).getText();
    }

    // TODO:
    // только первую +
    // чекать что уже нет аннотации +
    // сделать рабочий фикс +-
    // тип вывести
    //

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        PsiElement parent = element.getParent();
        if (!(parent instanceof FregeFunctionName)) {
            return;
        }

        if (!FregePsiUtilImpl.isInGlobalScope(element)) {
            return;
        }

        FregeFunctionNameImpl functionName = (FregeFunctionNameImpl) parent;
        if (!functionName.isFunctionBinding()) {
            return;
        }

        List<PsiElement> sameFunctionsInScope = getSameFunctionInScope(element);

        if (hasAnnotation(element, sameFunctionsInScope)) {
            return;
        }

        FregeBinding firstBinding = getFirstBinding(element, sameFunctionsInScope);

        if (firstBinding != FregePsiUtilImpl.getParentBinding(element)) {
            return;
        }

        FregeTopDecl addBefore = PsiTreeUtil.getParentOfType(firstBinding, FregeTopDecl.class);

        holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "No annotation for function provided")
                .range(element.getTextRange())
                .withFix(new FregeAddAnnotationQuickFix(element, addBefore))
                .create();


        // TODO remove
        String fullDefinitionTMP = getFullFunctionDefinitionsText(element, sameFunctionsInScope);
        System.err.println(element.getText());
        System.err.println(fullDefinitionTMP);
        System.err.println("\n");
    }
}
