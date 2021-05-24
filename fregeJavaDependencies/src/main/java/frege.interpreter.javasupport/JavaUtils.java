package frege.interpreter.javasupport;

import frege.runtime.Runtime;

import java.io.BufferedReader;
import java.io.FilePermission;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.Permission;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class JavaUtils {

    public static Object fieldValue(final String className,
                                    final String variableName, final InterpreterClassLoader loader) {
        final Class<?> clazz;
        try {
            clazz = loader.loadClass(className);
            return unwrapThunk(clazz.getDeclaredField(variableName).get(null));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object unwrapThunk(Object possibleThunk) {
        if (possibleThunk instanceof frege.run8.Thunk) {
            return ((frege.run8.Thunk<?>) possibleThunk).call();
        } else if (possibleThunk instanceof frege.run7.Thunk) {
            return ((frege.run7.Thunk<?>) possibleThunk).call();
        } else {
            return possibleThunk;
        }
    }

    public static Object sandboxFieldValue(final String className,
                                           final String variableName,
                                           final String stdinStr,
                                           final StringWriter outWriter,
                                           final StringWriter errWriter,
                                           final InterpreterClassLoader loader) {
        return sandbox(new FutureTask<>(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return fieldValueWithRuntime(className, variableName, stdinStr, outWriter, errWriter, loader);
            }
        }), 5, TimeUnit.SECONDS);
    }

    public static Object fieldValueWithRuntime(final String className,
                                               final String variableName,
                                               final String stdinStr,
                                               final StringWriter outWriter,
                                               final StringWriter errWriter,
                                               final InterpreterClassLoader loader) {
        prepareRuntime(stdinStr, outWriter, errWriter);
        return fieldValue(className, variableName, loader);
    }

    private static void prepareRuntime(final String stdinStr, final StringWriter outWriter, final StringWriter errWriter) {
        final BufferedReader stdin = new BufferedReader(new StringReader(stdinStr + "\n"));
        final PrintWriter stdout = new PrintWriter(outWriter);
        final PrintWriter stderr = new PrintWriter(errWriter);
        Runtime.stdin.set(stdin);
        Runtime.stdout.set(stdout);
        Runtime.stderr.set(stderr);
    }

    public static <V> V sandbox(final FutureTask<V> task,
                                final long timeout,
                                final TimeUnit unit
    ) {

        final Thread thread = new Thread(task);
        final SecurityManager oldSecurityManager = System.getSecurityManager();
        final AtomicBoolean isDisabled = new AtomicBoolean(false);
        final SecurityManager securityManager = new SecurityManager() {
            @Override
            public void checkPermission(final Permission perm) {
                if (!isDisabled.get()) {
                    if (perm instanceof RuntimePermission) {
                        final RuntimePermission runtimePerm = (RuntimePermission) perm;
                        if (runtimePerm.getName().equals("accessDeclaredMembers")) {
                            return;
                        }
                    } else if (perm instanceof FilePermission) {
                        final FilePermission filePerm = (FilePermission) perm;
                        final String fileName = filePerm.getName();
                        if (filePerm.getActions().equals("read") &&
                            (fileName.endsWith(".jar") || fileName.endsWith(".class"))) {
                            return;
                        }
                    }
                    super.checkPermission(perm);
                }
            }
        };
        try {
            System.setSecurityManager(securityManager);
            thread.start();
            return task.get(timeout, unit);
        } catch (Exception e) {
            isDisabled.set(true);
            task.cancel(true);
            thread.stop();
            throw new RuntimeException(e);
        } finally {
            isDisabled.set(true);
            System.setSecurityManager(oldSecurityManager);
        }
    }

}
