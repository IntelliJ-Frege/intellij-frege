package com.plugin.frege.parser;

import com.intellij.testFramework.ParsingTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

public abstract class FregeParsingTestAbstract extends ParsingTestCase {
    public FregeParsingTestAbstract() {
        super("", "fr", new FregeParserDefinition());
    }

    protected void doSuccessfulParsingTest() throws IOException {
        String name = getTestName();
        parseFile(name, loadFile(name + "." + myFileExt));
        ensureNoErrorElements();
    }

    protected void doFailParsingTest() throws IOException {
        boolean parsingFail = false;
        try {
            doSuccessfulParsingTest();
        } catch (AssertionError ignored) {
            parsingFail = true;
        }
        if (!parsingFail) {
            fail("Successful parse file " + myFile.getName());
        }
    }

    protected void doAccurateTest() {
        doTest(true);
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }

    @Override
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    protected abstract String getTestDataPath();
}
