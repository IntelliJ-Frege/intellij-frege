package com.plugin.frege.linemarker;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.PsiElement;
import com.plugin.frege.psi.FregeFunctionName;
import com.plugin.frege.psi.impl.FregePsiUtilImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FregeRunLineMarkerContributor extends RunLineMarkerContributor {
    @Override
    public @Nullable Info getInfo(@NotNull PsiElement element) {
        if (element instanceof FregeFunctionName &&
            FregePsiUtilImpl.isInGlobalScope(element) &&
            element.getText().equals("main")) {
            final String moduleName = FregePsiUtilImpl.getModuleName(element);
            if (moduleName == null) return null;

            PsiElement functionLHS = element.getParent();
            if (functionLHS.getChildren().length > 2) return null; // main can have 0 or 1 argument

            return new Info(AllIcons.RunConfigurations.TestState.Run,
                    new AnAction[]{
                            new FregeRunAction(moduleName)},
                    (PsiElement t) -> "Run " + moduleName);
        }
        return null;
    }


}
