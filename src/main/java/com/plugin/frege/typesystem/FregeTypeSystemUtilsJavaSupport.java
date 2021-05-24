package com.plugin.frege.typesystem;

import com.plugin.frege.typesystem_fr.FregeTypeSystemUtils;
import frege.run8.Thunk;

public class FregeTypeSystemUtilsJavaSupport {
    public static String getTypeOfByFullText(String moduleText, String funcName) throws TypeSystemException {
        FregeTypeSystemUtils utils = new FregeTypeSystemUtils();
        var typeResult = utils.getTypeOfByFullText(Thunk.lazy(FregeTypeSystemUtilsJavaSupport.class.getClassLoader()), Thunk.<String>lazy(moduleText), funcName);
        if (utils.isFailure(typeResult)) {
            String errorMessage = utils.getErrorMessage(typeResult);
            throw new TypeSystemException(errorMessage);
        }
        String type = utils.getType(typeResult);
        return type;
    }

    public static void main(String[] args) {
        try {
            System.out.println(getTypeOfByFullText("text1 a = a 1", "text2"));
        } catch (TypeSystemException e) {
            System.err.println(e.getMessage());
        }
    }
}
