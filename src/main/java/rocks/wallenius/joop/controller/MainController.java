package rocks.wallenius.joop.controller;

import rocks.wallenius.joop.compiler.CompilationException;
import rocks.wallenius.joop.compiler.CompilerUtil;
import rocks.wallenius.joop.configuration.ConfigurationService;
import rocks.wallenius.joop.model.Model;
import rocks.wallenius.joop.model.entity.JoopClass;
import rocks.wallenius.joop.model.entity.Tab;
import rocks.wallenius.joop.util.ClassFileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by philipwallenius on 23/05/2016.
 */
class MainController {

    private final static String CONF_KEY_SOURCES_DIR = "sources.directory";
    private final static String CONF_KEY_COMPILATION_DIR = "compilation.directory";

    private static ConfigurationService config;
    private List<JoopClass> classes;

    MainController(List<JoopClass> classes) {
        config = ConfigurationService.getInstance();
        this.classes = classes;
    }

    /**
     * Creates a new class and opens a new tab with a CodeArea
     *
     * @param fullyQualifiedName of new class
     * @throws IOException
     * @throws URISyntaxException
     */
    JoopClass createClass(String fullyQualifiedName) throws IOException, URISyntaxException {
        String className = fullyQualifiedName;
        if(fullyQualifiedName.contains(".")) {
            className = fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf(".") + 1);
        }

        Path path = new File(String.format("%s%s.java", config.getString(CONF_KEY_SOURCES_DIR), className)).toPath();
        JoopClass newClass = new JoopClass(fullyQualifiedName, path);
        newClass.setDefinition(ClassFileUtils.loadClassTemplate(fullyQualifiedName));
        classes.add(newClass);
        return newClass;

    }

    /**
     * Opens a class from file
     * @param file to open class from
     * @throws IOException
     */
    JoopClass openClass(File file) throws IOException {

        String classDefinition = ClassFileUtils.loadClass(file);

        String className = file.getName();

        // standardize the name of the new class
        if (className.toLowerCase().endsWith(".java")) {
            className = className.substring(0, className.lastIndexOf("."));
        }

        String fullyQualifiedName = ClassFileUtils.getFullyQualifiedName(className, classDefinition);

        JoopClass openedClass = new JoopClass(fullyQualifiedName, file.toPath());
        openedClass.setDefinition(classDefinition);
        classes.add(openedClass);
        return openedClass;

    }

    void saveClass(String fullyQualifiedName, String definition) throws IOException {
        JoopClass clazz = getClass(fullyQualifiedName);
        clazz.setDefinition(definition);
        ClassFileUtils.saveClass(clazz);
    }

    void closeClass(String fullyQualifiedName) {
        JoopClass clazz = getClass(fullyQualifiedName);
        classes.remove(clazz);
    }

    private JoopClass getClass(String fullyQualifiedName) {
        JoopClass result = null;
        for(JoopClass clazz : classes) {
            if(clazz.getFullyQualifiedName().equals(fullyQualifiedName)) {
                result = clazz;
            }
        }
        return result;
    }

    /**
     * Compiles classes
     * @throws CompilationException
     * @throws IOException
     */
    void compileClasses() throws CompilationException, IOException, ClassNotFoundException {

        if(classes.size() > 0) {
            List<File> fileList = classes.stream()
                    .map(JoopClass::getPath)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            CompilerUtil.compile(fileList.toArray(new File[fileList.size()]));
        }

    }

    /**
     * Loads compiled classes dynamically
     */
    void loadClasses() throws MalformedURLException, ClassNotFoundException {
        for(JoopClass clazz : classes) {

            URL[] urls = new URL[] { new URL("file:" + config.getString(CONF_KEY_COMPILATION_DIR)) };
            //Create a new URLClassLoader with url to directory
            ClassLoader classLoader = new URLClassLoader(urls);

            //Load the class and return it
            Class loadedClass = classLoader.loadClass(clazz.getNameWithoutFileExtension());
            clazz.setLoadedClass(loadedClass);

        }
    }

}
