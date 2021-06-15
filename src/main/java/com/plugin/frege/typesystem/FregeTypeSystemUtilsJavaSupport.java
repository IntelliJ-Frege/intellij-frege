package com.plugin.frege.typesystem;

//import com.plugin.frege.typesystem_fr.FregeTypeSystemUtils;
import frege.run8.Thunk;

public class FregeTypeSystemUtilsJavaSupport {
//    public static String getTypeOfByFullText(ClassLoader loader, String moduleText, String funcName) throws TypeSystemException {
//            Class<?> aClass;
//        try {
//            aClass = loader.loadClass("frege.Prelude");
//            System.err.println("Loaded!");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        moduleText = "module frege.ide.A where\n" +
//                "\n" +
//                "a x = 1\n";
//        funcName = "a";
//        System.err.println("HERE BEGIN GET TYPE");
////        ClassLoader tmp = Thread.currentThread().getContextClassLoader();
////        new FregeTypeSystemUtils().main(new String[]{moduleText, funcName});
//        FregeTypeSystemUtils utils = new FregeTypeSystemUtils();
//        var typeResult = utils.getTypeOfByFullText(Thunk.lazy(loader), Thunk.<String>lazy(moduleText), funcName);
//        if (utils.isFailure(typeResult)) {
//            String errorMessage = utils.getErrorMessage(typeResult);
//            throw new TypeSystemException(errorMessage);
//        }
//        String type = utils.getType(typeResult);
//        System.err.println("HERE");
//        return type;
//    }
//
//    public static void main(String[] args) {
////        new FregeTypeSystemUtils().main(new String[]{});
//        System.err.println("HERE BEGIN MAIN");
//        try {
//            System.err.println(getTypeOfByFullText(Thread.currentThread().getContextClassLoader(), "module frege.ide.A where\n" +
//                    "\n" +
//                    "a x = 1\n", "a"));
//        } catch (TypeSystemException e) {
//            System.err.println("THROWED");
//            System.err.println(e.getMessage());
//        }
//    }
}
