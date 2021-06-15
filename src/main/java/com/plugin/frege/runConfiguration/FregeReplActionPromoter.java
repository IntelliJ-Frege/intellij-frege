package com.plugin.frege.runConfiguration;

import com.intellij.openapi.actionSystem.ActionPromoter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.actions.EnterAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FregeReplActionPromoter implements ActionPromoter {
    // moves EnterAction to the end of actions list, so Repl action is performed first
    @Override
    public List<AnAction> promote(@NotNull List<? extends AnAction> actions, @NotNull DataContext context) {
        List<AnAction> result = new ArrayList<AnAction>();
        for (AnAction action : actions) {
            if (!(action instanceof EnterAction))
                result.add(action);
        }
        for (AnAction action : actions) {
            if ((action instanceof EnterAction))
                result.add(action);
        }
        return result;
    }
}
