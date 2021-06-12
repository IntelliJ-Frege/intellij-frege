package com.plugin.frege.parser;

import java.io.IOException;

public class FregeParseProgramsWithSamplePsi extends FregeParsingTestAbstract {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/parsing/programsWithSamplePsi";
    }

    public void testQLexOperatorIsNotPrimary() throws IOException {
        doAccurateTest();
    }

    public void testQualifiedConstructorIsNotComposition() throws IOException {
        doAccurateTest();
    }
}
