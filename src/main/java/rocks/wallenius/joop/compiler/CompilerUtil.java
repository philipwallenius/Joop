package rocks.wallenius.joop.compiler;

import rocks.wallenius.joop.configuration.ConfigurationService;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by philipwallenius on 18/02/16.
 */
public class CompilerUtil {

    private final static String CONF_COMPILATION_DIR = "compilation.directory";

    private static ConfigurationService config;

    static {
        config = ConfigurationService.getInstance();
    }

    private CompilerUtil() {}

    public static void compile(File[] files) throws IOException {

        Files.createDirectories(new File(config.getString(CONF_COMPILATION_DIR)).toPath());

        try {
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            List<String> optionList = new ArrayList<String>();
            optionList.add("-classpath");
            optionList.add(System.getProperty("java.class.path")+";"+ config.getString(CONF_COMPILATION_DIR));
            optionList.add("-d");
            optionList.add(config.getString(CONF_COMPILATION_DIR));

            Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null, compilationUnit);

            if (task.call()) {
                System.out.println("Compiled classes successfully!");
            } else {
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    System.out.format("Error on line %d in %s%n",
                            diagnostic.getLineNumber(),
                            diagnostic.getSource().toUri());
                }
            }
            fileManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
