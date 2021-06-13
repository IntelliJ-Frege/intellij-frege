package com.plugin.frege.parser;

import java.io.IOException;

public class FregeSuccessfulParsingValidProgramsTest extends FregeParsingTestAbstract {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/parsing/validPrograms";
    }

    public void testClassDcl() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testDataDcl() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testImports() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testInstanceDcl() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testSimpleProgram() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testTypes() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testLayoutRule() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testList() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testDoExpression() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testPatterns() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testCaseEx() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testDataRecord() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testExtraSemicolons() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testNewtype() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testNativeFunction() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testDocumentation() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testDots() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testPrimary() throws IOException {
        doSuccessfulParsingTest();
    }

    public void testVaridConidQuotes() throws IOException {
        doSuccessfulParsingTest();
    }
}
