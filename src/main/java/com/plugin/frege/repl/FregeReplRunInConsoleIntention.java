package com.plugin.frege.repl;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class FregeReplRunInConsoleIntention implements IntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Run selected text in Frege REPL";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Frege REPL";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return editor.getSelectionModel().hasSelection();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        FregeReplRunInConsole.invokeRunInConsole(project, editor);
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
