package com.plugin.frege.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;

import static com.plugin.frege.psi.FregeTypes.*;

public class FregeParserUtil extends GeneratedParserUtilBase {
    public static boolean javaCodeParseExternal(PsiBuilder builder, int level) {
        int balance = 0;
        while (balance >= 0 && !builder.eof()) {
            if (LEFT_BRACE.equals(builder.getTokenType())) {
                balance++;
            } else if (RIGHT_BRACE.equals(builder.getTokenType())) {
                balance--;
            }
            if (balance >= 0) {
                builder.advanceLexer();
            }
        }
        return balance == -1;
    }
}
