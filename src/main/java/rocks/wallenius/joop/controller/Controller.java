package rocks.wallenius.joop.controller;

import rocks.wallenius.joop.model.JoopObject;
import rocks.wallenius.joop.model.Model;
import rocks.wallenius.joop.model.Source;
import rocks.wallenius.joop.view.View;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by philipwallenius on 23/05/2016.
 */
public class Controller {

    private static final String COMPILATION_DIR = "classes/";
    private Model model;
    private View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    /**
     * Creates a new class and opens a new tab with a CodeArea
     *
     * @param className of new class
     * @throws IOException
     * @throws URISyntaxException
     */
    public Source create(String className) throws IOException, URISyntaxException {

        className = removeFileEndingFromClassName(className);

        Source source = new Source(className, ClassFileUtils.loadClassTemplate(className));

        model.addSource(source);
        return source;

    }

    /**
     * Opens a class from file
     * @param file to open class from
     * @throws IOException
     */
    public Source open(File file) throws IOException {
        String className = file.getName();

        className = removeFileEndingFromClassName(className);

        Source source = new Source(className, ClassFileUtils.loadClass(file), file);

        model.addSource(source);
        return source;

    }

    public void close(Source source) {
        this.model.removeSource(source);
    }

    public void save(Source source) throws IOException {
        ClassFileUtils.saveClass(source);
    }

    public void saveAll() throws IOException {
        for (Source source : model.getSources()) {
            save(source);
        }
    }

    public void compile() throws CompilationException, IOException, ClassNotFoundException {

        List<File> files = model.getSources().stream().peek(source -> {
            if (source.getFile() == null) {
                throw new RuntimeException("Unable to compile. One or more source files are null.");
            }
        }).map(Source::getFile).collect(Collectors.toList());

        Files.createDirectories(new File(COMPILATION_DIR).toPath());

        if(files.size() == 0) {
            throw new RuntimeException("Found no source files to compile");
        }

        compileFiles(files.toArray(new File[files.size()]));
        loadClasses();
        view.updateView(model);

    }

    private String removeFileEndingFromClassName(String className) {
        if (className.toLowerCase().endsWith(".java")) {
            className = className.substring(0, className.lastIndexOf("."));
        }
        return className;
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

    /**
     * Loads compiled joopClasses dynamically
     */
    public void loadClasses() throws MalformedURLException, ClassNotFoundException {
        model.clearClasses();
        model.clearObjects();

        for(Source source : model.getSources()) {

            URL[] urls = new URL[] { new URL("file:" + COMPILATION_DIR) };
            //Create a new URLClassLoader with url to directory
            ClassLoader classLoader = new URLClassLoader(urls);

            //Load the class and return it
            Class loadedClass = classLoader.loadClass(source.getClassName());
            model.addClass(loadedClass);
        }
    }

    public boolean hasAnySourceUnsavedChanges() {
        boolean result;

        result = model.getSources().stream().anyMatch(source -> {

            File file = source.getFile();

            if(file == null) {
                return true;
            }

            String codeInFile;

            try {
                codeInFile = ClassFileUtils.loadClass(file);

            } catch (IOException e) {
                throw new RuntimeException(String.format("Unable to check for unsaved changes in source %s", source.getClassName()));
            }

            return !source.getCode().equals(codeInFile);
        });

        return result;
    }

    public void invokeConstructor(Class clazz, String instanceName, Class[] parameters, Object[] arguments) {

        try {
            Constructor constructor = clazz.getConstructor(parameters);
            Object instance = constructor.newInstance(arguments);
            model.addObject(new JoopObject(clazz.getCanonicalName(), instanceName, instance));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        view.updateView(model);

    }

    public Object invokeMethod(Object object, String methodName, Class[] parameters, Object[] arguments) {

        Object returnValue = null;
        try {
            Method method = object.getClass().getDeclaredMethod(methodName, parameters);
            method.setAccessible(true);
            returnValue = method.invoke(object, arguments);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        view.updateView(model);

        return returnValue;
    }

}
