package com.plugin.frege.lexer;

import com.intellij.lexer.FlexAdapter;

public class FregeLexerAdapter extends FlexAdapter {

    public FregeLexerAdapter() {
        super(new FregeLexer(null));
    }
}
