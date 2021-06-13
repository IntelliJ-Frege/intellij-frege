package com.plugin.frege.lexer.layout;

import org.jetbrains.annotations.NotNull;

import java.util.Stack;

public class FregeLayoutLexerStack {
    private final @NotNull Stack<@NotNull Integer> indentStack = new Stack<>();
    private final @NotNull Stack<@NotNull Integer> braceStack = new Stack<>();
    private int braceLevel = 0;

    public FregeLayoutLexerStack() {
        indentStack.push(-1); // bottom section
        indentStack.push(0); // global section
    }

    public void enterLeftBrace() {
        braceLevel++;
        braceStack.push(braceLevel);
        indentStack.push(-1);
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

    public void enterVirtualSectionStart(int level) {
        indentStack.push(level);
    }

    public int getCurrentIndentLevel() {
        return indentStack.peek();
    }

    public void enterVirtualSectionEnd() {
        indentStack.pop();
    }
}
