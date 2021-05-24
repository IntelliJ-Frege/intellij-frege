package frege.interpreter.javasupport;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.xml.stream.events.Attribute;
import java.util.Locale;

public class CompilationInfo {

    private final boolean isSuccess;
    private final DiagnosticCollector<JavaFileObject> diagnostics;

    public CompilationInfo(final boolean isSuccess,
                           final DiagnosticCollector<JavaFileObject> diagnostics) {
        this.isSuccess = isSuccess;
        this.diagnostics = diagnostics;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String errorsAsString() {
        final StringBuilder msgs = new StringBuilder();
        for (final Diagnostic<? extends JavaFileObject> diagnostic : diagnostics
            .getDiagnostics()) {
            final JavaFileObject source = diagnostic.getSource();
            final String message = String.format("Error:%s[%s:%s]: %s\n",
                source != null ? source.getName() : "<unknown source>",
                diagnostic.getLineNumber(), diagnostic.getColumnNumber(),
                diagnostic.getMessage(Locale.getDefault()));
            msgs.append(message);
        }
        return msgs.toString();
    }

}
