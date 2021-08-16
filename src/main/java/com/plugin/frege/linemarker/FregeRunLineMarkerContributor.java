package com.plugin.frege.linemarker;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugin.frege.psi.FregeFunctionName;
import com.plugin.frege.psi.impl.FregeBindingImpl;
import com.plugin.frege.psi.util.FregePsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FregeRunLineMarkerContributor extends RunLineMarkerContributor {
    @Override
    public @Nullable Info getInfo(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        if (!(parent instanceof FregeFunctionName)) {
            return null;
        }
        FregeBindingImpl binding = PsiTreeUtil.getParentOfType(parent, FregeBindingImpl.class);
        if (binding == null || !binding.isMainFunctionBinding()) {
            return null;
        }

        final String moduleName = FregePsiUtil.getModuleName(element);
        if (moduleName == null) {
            return null;
        }

        return new Info(AllIcons.RunConfigurations.TestState.Run,
                new AnAction[]{
                        new FregeRunAction(moduleName)},
                (PsiElement t) -> "Run " + moduleName);
    }
}
