package rocks.wallenius.joop.service;

import rocks.wallenius.joop.exception.CompilationException;
import rocks.wallenius.joop.domain.JoopClass;
import rocks.wallenius.joop.repository.ClassRepository;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by philipwallenius on 28/07/2019.
 */
public class ClassServiceImpl implements ClassService {

    private static final String COMPILATION_DIR = "classes/";
    private ClassRepository repository;

    public ClassServiceImpl(ClassRepository repository) {
        this.repository = repository;
    }

    @Override
    public JoopClass compile(File[] files) throws CompilationException, IOException {

        Files.createDirectories(new File(COMPILATION_DIR).toPath());

        compileFiles(files);


        return null;

    }

    private static void compileFiles(File[] files) throws CompilationException, IOException {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        List<String> optionList = new ArrayList<>();
        optionList.add("-parameters");
        optionList.add("-classpath");
        optionList.add(System.getProperty("java.class.path") + ";" + COMPILATION_DIR);
        optionList.add("-d");

        optionList.add(COMPILATION_DIR);

        Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null, compilationUnit);

        if (!task.call()) {
            throw new CompilationException(diagnostics.getDiagnostics());
        }
        fileManager.close();
    }
}
