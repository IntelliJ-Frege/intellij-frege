package com.plugin.frege.parser;

import com.intellij.testFramework.ParsingTestCase;

public class FregeParsingTest extends ParsingTestCase {
    public FregeParsingTest() {
        super("", "fr", new FregeParserDefinition());
    }

    public void testClassDcl() {
        doTest(true);
    }

    public void testDataDcl() {
        doTest(true);
    }

    public void testImports() {
        doTest(true);
    }

    public void testInstanceDcl() {
        doTest(true);
    }

    public void testSimpleProgram() {
        doTest(true);
    }

    public void testTypes() {
        doTest(true);
    }

    public void testLayoutRule() {
        doTest(true);
    }

    public void testList() {
        doTest(true);
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/psi";
    }
}
