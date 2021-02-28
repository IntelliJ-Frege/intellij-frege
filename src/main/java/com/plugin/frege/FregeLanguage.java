package com.plugin.frege;

import com.intellij.lang.Language;

public class FregeLanguage extends Language {
    public static final FregeLanguage INSTANCE = new FregeLanguage();

    private FregeLanguage() {
        super("Frege");
    }
}
