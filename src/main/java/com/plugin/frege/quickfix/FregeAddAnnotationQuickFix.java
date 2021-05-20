package com.plugin.frege.quickfix;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.plugin.frege.psi.FregeElementFactory;
import org.jetbrains.annotations.NotNull;

public class FregeAddAnnotationQuickFix extends BaseIntentionAction {
    private final PsiElement toAnnotate;
    private final PsiElement toAddBefore;

    public FregeAddAnnotationQuickFix(PsiElement toAnnotate, PsiElement toAddBefore) {
        this.toAnnotate = toAnnotate;
        this.toAddBefore = toAddBefore;
    }

    @Override
    public @IntentionName @NotNull String getText() {
        return "Add annotation text"; // TODO
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Add annotation family name";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true; // TODO ???
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        toAddBefore.getParent().addBefore(FregeElementFactory.createNewLine(project), toAddBefore);
        toAddBefore.getParent().addBefore(FregeElementFactory.createAnnotation(project, toAnnotate.getText(), "myType"), toAddBefore);
        toAddBefore.getParent().addBefore(FregeElementFactory.createNewLine(project), toAddBefore);
        // TODO do we need to create FregeAnnotation, or better create FregeTopDecl instead?
    }
}
