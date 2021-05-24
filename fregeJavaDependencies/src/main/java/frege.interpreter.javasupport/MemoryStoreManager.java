package frege.interpreter.javasupport;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardLocation;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class MemoryStoreManager extends
	ForwardingJavaFileManager<JavaFileManager> {

	private static final String JAVA_EXTENSION = ".java";
	private final Map<String, MemoryJavaClass> byteCodeMap;
	private final Map<URI, JavaFileObject> sourceMap;
	private final InterpreterClassLoader classLoader;

	public MemoryStoreManager(final JavaFileManager fileManager,
							  final InterpreterClassLoader classLoader) {
		super(fileManager);
		this.classLoader = classLoader;
		this.byteCodeMap = toMemoryJavaClassMap(classLoader.classes());
		sourceMap = new HashMap<>();
	}

	private Map<String, MemoryJavaClass> toMemoryJavaClassMap(final Map<String, byte[]> byteCodeMap) {
		Map<String, MemoryJavaClass> result = new HashMap<>();
		for (Map.Entry<String, byte[]> classNameByteCode: byteCodeMap.entrySet())
			result.put(classNameByteCode.getKey(),
				new MemoryJavaClass(classNameByteCode.getKey(), classNameByteCode.getValue()));

		return result;
	}

	@Override
	public String inferBinaryName(final Location location,
			final JavaFileObject file) {
		if (file instanceof MemoryJavaClass) {
			return file.getName();
		} else {
			return super.inferBinaryName(location, file);
		}
	}

	@Override
	public Iterable<JavaFileObject> list(final Location location,
			final String packageName, final Set<Kind> kinds,
			final boolean recurse) throws IOException {
		final Iterable<JavaFileObject> result = super.list(location,
				packageName, kinds, recurse);
		final ArrayList<JavaFileObject> files = new ArrayList<>();
		if (location == StandardLocation.CLASS_PATH
				&& kinds.contains(Kind.CLASS)) {
			for (final JavaFileObject file : byteCodeMap.values()) {
                if (file.getName().startsWith(packageName))
					files.add(file);
			}
		} else if (location == StandardLocation.SOURCE_PATH
				&& kinds.contains(Kind.SOURCE)) {
			for (final JavaFileObject file : sourceMap.values()) {
				if (file.getName().startsWith(packageName))
					files.add(file);
			}
		}
		for (final JavaFileObject file : result) {
			files.add(file);
		}
        return files;
	}

	private static class MemoryJavaSource extends SimpleJavaFileObject {
		private final String code;

		private MemoryJavaSource(final String name, final String code) {
			super(toURI(StandardLocation.SOURCE_PATH, name), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharBuffer getCharContent(final boolean ignoreEncodingErrors) {
			return CharBuffer.wrap(code);
		}

		@Override
		public Reader openReader(final boolean ignoreEncodingErrors)
				throws IOException {
			return new StringReader(code);
		}

        @Override
        public String toString() {
            return String.format("MemoryJavaSource[%s, %s]", getName(), code);
        }
    }

	private class MemoryJavaClass extends SimpleJavaFileObject {
		private final String name;
		private ByteArrayOutputStream byteCodeOutputStream;
		private byte[] bytecode;

		private MemoryJavaClass(final String name) {
            super(toURI(StandardLocation.CLASS_PATH, name), Kind.CLASS);
            byteCodeMap.put(name, this);
			this.name = name;
		}

        private MemoryJavaClass(final String name, final byte[] bytecode) {
			super(toURI(StandardLocation.CLASS_PATH, name), Kind.CLASS);
			this.name = name;
			this.bytecode = bytecode;
		}

		@Override
		public OutputStream openOutputStream() {
			byteCodeOutputStream = new ByteArrayOutputStream();
			return byteCodeOutputStream;
		}

		@Override
		public InputStream openInputStream() throws IOException {
			return new ByteArrayInputStream(getByteCode());
		}

		public byte[] getByteCode() {
			return bytecode != null ? bytecode : byteCodeOutputStream.toByteArray();
		}

		@Override
		public String getName() {
			return name;
		}

        @Override
        public String toString() {
            return String.format("MemoryJavaClass[%s]", name);
        }
    }

	@Override
	public ClassLoader getClassLoader(final Location location) {
		return getClassLoader();
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}

	InterpreterClassLoader getClassLoader() {
		Collection<MemoryJavaClass> bytecodes = byteCodeMap.values();
		Map<String, byte[]> bytecodeMap = new HashMap<>();
        for (MemoryJavaClass memoryJavaClass: bytecodes) {
            bytecodeMap.put(memoryJavaClass.getName(), memoryJavaClass.getByteCode());
        }
		return new InterpreterClassLoader(classLoader.getParent(), bytecodeMap);
	}

	@Override
	public JavaFileObject getJavaFileForOutput(final Location location,
			final String name, final Kind kind, final FileObject sibling)
			throws IOException {
        final String className = toClassName(name);
		if (kind == Kind.CLASS) {
			return new MemoryJavaClass(className);
		} else {
			return super.getJavaFileForOutput(location, name, kind,
					sibling);
		}
	}

    private static String toClassName(final String name) {
        return name.replaceAll("[/\\\\]", ".");
    }

	public void putFileForInput(final Location location,
			final String packageName, final String relativeName,
			final JavaFileObject file) {
		sourceMap.put(toURI(location, packageName, relativeName), file);
	}

	public void putFileForInput(final String javaFilePath,
			final JavaFileObject file) {
		sourceMap.put(toURI(StandardLocation.SOURCE_PATH, javaFilePath), file);
	}

	@Override
	public FileObject getFileForInput(final Location location,
			final String packageName, final String relativeName)
			throws IOException {
		final URI uri = toURI(location, packageName, relativeName);
		if (sourceMap.containsKey(uri)) {
			return sourceMap.get(uri);
		} else {
			return super.getFileForInput(location, packageName, relativeName);
		}
	}

    public static JavaFileObject makeStringSource(final String name,
			final String code) {
		return new MemoryJavaSource(name, code);
	}

    private URI toURI(final Location location, final String packageName, final String relativeName) {
        String name = packageName + "." + relativeName;
        return toURI(location, name);
    }

	private static URI toURI(final Location location, final String name) {
		final String qualifiedClassName;
        final String extension;
        if (location == StandardLocation.SOURCE_PATH) {
			final String noExtPath = name.endsWith(JAVA_EXTENSION) ? name
					.substring(0, name.lastIndexOf('.')) : name;
			qualifiedClassName = noExtPath.replace('/', '.').replace('\\', '.');
            extension = JAVA_EXTENSION;
		} else { // Class Path
			qualifiedClassName = name.replace("[/\\\\]", ".");
            extension = "";
		}
		final String className = className(qualifiedClassName);
		final int lastDot = qualifiedClassName.lastIndexOf('.');
		final URI uri;
		if (lastDot < 0) {
			uri = createUri(location, className, extension);
		} else {
			final String packName = qualifiedClassName.substring(0, lastDot);
			uri = createUri(location, packName, className, extension);
		}
        return uri;
	}

    private static URI createUri(final Location location, final String className, final String extension) {
        final String classNameWithSep = className.replace('.', '/');
        final String uriString = String.format("file:///%s%s", classNameWithSep, extension);
        return URI.create(uriString);
    }

    private static URI createUri(final Location location,
                                 final String packName,
                                 final String className,
                                 final String extension) {
        final String classNameWithSep = className.replace('.', '/');
        final String packNameWithSep = packName.replace('.', '/');
        final String uriString = String.format("file:///%s/%s%s", packNameWithSep,
            classNameWithSep, extension);
        return URI.create(uriString);
    }

    private static String className(final String qualifiedClassName) {
        return qualifiedClassName.substring(qualifiedClassName.lastIndexOf('.') + 1);
    }
}