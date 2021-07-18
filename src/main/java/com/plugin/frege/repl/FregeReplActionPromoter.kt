package com.plugin.frege.repl

import com.intellij.openapi.actionSystem.ActionPromoter
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.actions.EnterAction

class FregeReplActionPromoter : ActionPromoter {
    // moves EnterAction to the end of actions list, so Repl action is performed first
    override fun promote(actions: List<AnAction>, context: DataContext): List<AnAction> {
        val result = mutableListOf<AnAction>()
        result += actions.filter { it !is EnterAction }
        result += actions.filter { it is EnterAction }
        return result
    }
}
