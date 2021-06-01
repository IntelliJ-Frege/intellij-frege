package com.plugin.frege.lexer.layout;

import org.jetbrains.annotations.NotNull;

import java.util.Stack;

public class FregeLayoutLexerStack {
    private final @NotNull Stack<@NotNull Integer> indentStack = new Stack<>();
    private final @NotNull Stack<@NotNull Integer> sectionGeneratingBrace = new Stack<>();
    private int braceLevel = 0;

    public FregeLayoutLexerStack() {
        indentStack.push(-1);
    }

    public void enterLeftBrace(boolean isSectionGenerating) {
        braceLevel++;
        if (isSectionGenerating) {
            sectionGeneratingBrace.push(braceLevel);
            indentStack.push(-1);
        }
    }

    public int enterRightBrace() {
        int skippedIndents = 0;
        if (!sectionGeneratingBrace.empty() &&
                sectionGeneratingBrace.peek() == braceLevel) {
            sectionGeneratingBrace.pop();
            while (!indentStack.empty() && indentStack.peek() >= 0) {
                skippedIndents++;
                indentStack.pop();
            }
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
