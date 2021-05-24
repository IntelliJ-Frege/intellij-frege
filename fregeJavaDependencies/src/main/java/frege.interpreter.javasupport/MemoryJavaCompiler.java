package frege.interpreter.javasupport;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryJavaCompiler {
    private final JavaCompiler compiler;
    private MemoryStoreManager fileManager;
    private final InterpreterClassLoader classLoader;

    public MemoryJavaCompiler(final InterpreterClassLoader classLoader) {
        compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Error: Java compiler not found. "
                + "Java Compiler API, tools.jar should be available on classpath. "
                + "Use the JVM that comes with the JDK, not JRE.");
        }
        fileManager = new MemoryStoreManager(compiler.getStandardFileManager(null, null, null), classLoader);
        this.classLoader = classLoader;
    }

    public CompilationInfo compile(final Map<String, CharSequence> sources,
                                   final Iterable<String> options) {
        final DiagnosticCollector<JavaFileObject> diagnostics =
            new DiagnosticCollector<>();
        final Iterable<? extends JavaFileObject> compilationUnits =
            toSourceFiles(sources, fileManager);
        final CompilationTask task = compiler.getTask(null, fileManager,
            diagnostics, options, null, compilationUnits);
        final boolean isSuccess = task.call();
        return new CompilationInfo(isSuccess, diagnostics);
    }

    public CompilationInfo compile(final Map<String, CharSequence> sources) {
        // TODO The version is hardcoded as the Frege compiler for Java 8 is still not available on Maven central
    	/*
    	 * Sorry, but I need a repl where I can just point to the latest compiler.
    	 *                                                -- Ingo
    	 */
    	
        final String version = "1.8"; //getJvmVersion();
        List<String> options = new ArrayList<>();
        options.add("-source");
        options.add(version);
        options.add("-target");
        options.add(version);

        if (classLoader.getParent() instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader) classLoader.getParent();
            options.add("-classpath");
            StringBuilder sb = new StringBuilder();
            for (URL url : urlClassLoader.getURLs())
                sb.append(url.getFile()).append(File.pathSeparator);
            options.add(sb.toString());
        }
        return compile(sources, options);
    }

    public CompilationInfo compile(
        final String sourceCode, final String className) {
        final Map<String, CharSequence> sources = new HashMap<>();
        sources.put(className, sourceCode);
        return compile(sources);
    }

    public InterpreterClassLoader classLoader() {
        return fileManager.getClassLoader();
    }

    private Iterable<? extends JavaFileObject> toSourceFiles(
        final Map<String, CharSequence> source, final MemoryStoreManager fileManager) {
        final List<JavaFileObject> files = new ArrayList<>();
        for (final Map.Entry<String, CharSequence> entry : source.entrySet()) {
            final JavaFileObject sourceFile = MemoryStoreManager.makeStringSource(entry.getKey(),
                entry.getValue().toString());
            files.add(sourceFile);
            fileManager.putFileForInput(entry.getKey(), sourceFile);
        }
        return files;
    }
}