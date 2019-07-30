package rocks.wallenius.joop.exception;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.List;
import java.util.Locale;

/**
 * Created by jpaw on 19/05/2016.
 */
public class CompilationException extends Exception {

    private List<Diagnostic<? extends JavaFileObject>> diagnostics;

    public CompilationException(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        super();
        this.diagnostics = diagnostics;
    }

    public String getCompilationExceptionMessage() {
        StringBuilder sb = new StringBuilder();
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
            sb.append(String.format("Error on line %s in %s.\n%s", diagnostic.getLineNumber(), diagnostic.getSource().toUri(), diagnostic.getMessage(Locale.ENGLISH))).append("\n");
        }
        return sb.toString();
    }

}
