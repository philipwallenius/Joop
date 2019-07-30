package rocks.wallenius.joop.compiler;

import rocks.wallenius.joop.configuration.ConfigurationService;
import rocks.wallenius.joop.exception.CompilationException;

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

    private final static String CONF_KEY_COMPILATION_DIR = "compilation.directory";

    private static ConfigurationService config;

    static {
        config = ConfigurationService.getInstance();
    }

    private CompilerUtil() {}

    public static void compile(File[] files) throws IOException, CompilationException {

        Files.createDirectories(new File(config.getString(CONF_KEY_COMPILATION_DIR)).toPath());


        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        List<String> optionList = new ArrayList<>();
        optionList.add("-parameters");
        optionList.add("-classpath");
        optionList.add(System.getProperty("java.class.path") + ";" + config.getString(CONF_KEY_COMPILATION_DIR));
        optionList.add("-d");

        optionList.add(config.getString(CONF_KEY_COMPILATION_DIR));

        Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null, compilationUnit);

        if (!task.call()) {
            throw new CompilationException(diagnostics.getDiagnostics());
        }
        fileManager.close();
    }

}
