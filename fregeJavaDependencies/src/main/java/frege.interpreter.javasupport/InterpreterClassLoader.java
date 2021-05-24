package frege.interpreter.javasupport;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class InterpreterClassLoader extends URLClassLoader {
    private final Map<String, byte[]> classes;

    public InterpreterClassLoader() {
        this(Thread.currentThread().getContextClassLoader(), new HashMap<>());
    }

    public InterpreterClassLoader(final Map<String, byte[]> classes) {
        this(Thread.currentThread().getContextClassLoader(), classes);
    }

    public InterpreterClassLoader(final ClassLoader parent) {
        this(parent, new HashMap<>());
    }

    public InterpreterClassLoader(final ClassLoader parent,
                                  final Map<String, byte[]> classFiles) {
        super(new URL[0], parent);
        this.classes = new HashMap<>(classFiles);
    }

    @Override
    protected Class<?> findClass(final String className)
            throws ClassNotFoundException {
        byte[] bytecode = classes.get(className);
        if (bytecode == null)
            bytecode = classes.get(className.replace('.', '/'));

        if (bytecode != null) return defineClass(className, bytecode, 0, bytecode.length);
        else {
            final Class<?> clazz = super.findClass(className);
            return clazz;
        }
    }

    @Override
    public InputStream getResourceAsStream(final String name) {
        final InputStream contents = super.getResourceAsStream(name);
        if (contents != null) {
            return contents;
        }
        if (name.endsWith(".class")) {
            final String noSuffix = name.substring(0, name.lastIndexOf('.'));
            final String relativeName;
            if (name.startsWith("/")) {
                relativeName = noSuffix.substring(1);
            } else {
                relativeName = noSuffix;
            }
            final String className = relativeName.replace('/', '.');
            final byte[] bytecode = classes.get(className);
            if (bytecode != null) {
                return new ByteArrayInputStream(bytecode);
            }
        }
        return null;
    }

    public Map<String, byte[]> classes() {
        return new HashMap<>(classes);
    }
}