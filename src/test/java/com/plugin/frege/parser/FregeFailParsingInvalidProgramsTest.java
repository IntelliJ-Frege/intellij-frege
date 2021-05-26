package com.plugin.frege.parser;

import java.io.IOException;

public class FregeFailParsingInvalidProgramsTest extends FregeParsingTestAbstract {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/parsing/invalidPrograms";
    }

    public void testNewtypeTwoConstructors() throws IOException {
        doFailParsingTest();
    }
}
