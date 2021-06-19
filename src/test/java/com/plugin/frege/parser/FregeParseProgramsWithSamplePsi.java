package com.plugin.frege.parser;

public class FregeParseProgramsWithSamplePsi extends FregeParsingTestAbstract {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/parsing/programsWithSamplePsi";
    }

    public void testQLexOperatorIsNotPrimary() {
        doAccurateTest();
    }

    public void testQualifiedConstructorIsNotComposition() {
        doAccurateTest();
    }
}
