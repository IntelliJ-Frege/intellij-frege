package com.plugin.frege.linemarker;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.plugin.frege.psi.FregeFunctionName;
import org.jetbrains.annotations.NotNull;

// TODO
// not yet implemented
public class FregeRunLineMarkerProvider implements LineMarkerProvider {
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
//      example of usage
        /*
        if (element instanceof FregeFunctionName)
            return new LineMarkerInfo<>(element, element.getTextRange(), AllIcons.RunConfigurations.TestState.Run,
                    (PsiElement t) -> "Run " + t.getText(), (e, t) -> {System.err.println(t.getText());}, GutterIconRenderer.Alignment.LEFT, () -> "Run");
        */
        return null;
    }
}
