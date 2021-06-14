package com.plugin.frege.lexer.layout;

import com.intellij.psi.tree.IElementType;
import com.plugin.frege.psi.FregeTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

public class FregeLayoutLexerStack {
    private final @NotNull Stack<@NotNull Integer> indentStack = new Stack<>();
    private final @NotNull Stack<@NotNull Integer> braceStack = new Stack<>();
    private final @NotNull Stack<@NotNull IElementType> regionsStack = new Stack<>();
    private int braceLevel = 0;

    public FregeLayoutLexerStack() {
        indentStack.push(-1); // bottom section
        indentStack.push(0); // global section
    }

    public void enterLeftBrace() {
        regionsStack.push(FregeTypes.LEFT_BRACE);
        braceLevel++;
        braceStack.push(braceLevel);
        indentStack.push(-1);
    }

    public void enterLeftParen() {
        regionsStack.push(FregeTypes.LEFT_PAREN);
    }

    public void enterLeftBracket() {
        regionsStack.push(FregeTypes.LEFT_BRACKET);
    }

    public int skipToBottom() {
        int skippedIndents = 0;
        while (!indentStack.empty() && indentStack.peek() >= 0) {
            skippedIndents++;
            indentStack.pop();
        }
        return skippedIndents;
    }

    public int enterRightBrace() {
        if (!regionsStack.empty()) {
            regionsStack.pop();
        }
        int skippedIndents = 0;
        if (!braceStack.empty() &&
                braceStack.peek() == braceLevel) {
            braceStack.pop();
            skippedIndents = skipToBottom();
            indentStack.pop();
        }
        braceLevel--;
        return skippedIndents;
    }

    public void enterRightParen() {
        if (!regionsStack.empty()) {
            regionsStack.pop();
        }
    }

    public void enterRightBracket() {
        if (!regionsStack.empty()) {
            regionsStack.pop();
        }
    }

    public @Nullable IElementType previousRegion() {
        return regionsStack.size() > 1 ? regionsStack.get(regionsStack.size() - 2) : null;
    }

    public @Nullable IElementType currentRegion() {
        return regionsStack.empty() ? null : regionsStack.peek();
    }

    public void enterVirtualSectionStart(int level) {
        regionsStack.push(FregeTypes.VIRTUAL_OPEN_SECTION);
        indentStack.push(level);
    }

    public int getCurrentIndentLevel() {
        return indentStack.peek();
    }

    public void enterVirtualSectionEnd() {
        if (!regionsStack.empty()) {
            regionsStack.pop();
        }
        indentStack.pop();
    }
}
