package rocks.wallenius.joop.controller;

import rocks.wallenius.joop.compiler.CompilationException;
import rocks.wallenius.joop.compiler.CompilerUtil;
import rocks.wallenius.joop.configuration.ConfigurationService;
import rocks.wallenius.joop.model.entity.JoopClass;
import rocks.wallenius.joop.model.entity.JoopObject;
import rocks.wallenius.joop.util.ClassFileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by philipwallenius on 23/05/2016.
 */
public class MainController {

    private final static String CONF_KEY_SOURCES_DIR = "sources.directory";
    private final static String CONF_KEY_COMPILATION_DIR = "compilation.directory";

    private static ConfigurationService config;
    private List<JoopClass> classes;
    private List<JoopObject> objects;

    public MainController(List<JoopClass> classes, List<JoopObject> objects) {
        config = ConfigurationService.getInstance();
        this.classes = classes;
        this.objects = objects;
    }

    /**
     * Creates a new class and opens a new tab with a CodeArea
     *
     * @param fullyQualifiedName of new class
     * @throws IOException
     * @throws URISyntaxException
     */
    public JoopClass createClass(String fullyQualifiedName) throws IOException, URISyntaxException {
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
    public JoopClass openClass(String className, File file) throws IOException {

        String classDefinition = ClassFileUtils.loadClass(file);

        String fullyQualifiedName = ClassFileUtils.getFullyQualifiedName(className, classDefinition);

        JoopClass openedClass = new JoopClass(fullyQualifiedName, file.toPath());
        openedClass.setDefinition(classDefinition);
        classes.add(openedClass);
        return openedClass;

    }

    public void saveClass(String fullyQualifiedName, String definition) throws IOException {
        JoopClass clazz = getClass(fullyQualifiedName);
        clazz.setDefinition(definition);
        ClassFileUtils.saveClass(clazz);
    }

    public void closeClass(String fullyQualifiedName) {
        JoopClass clazz = getClass(fullyQualifiedName);
        classes.remove(clazz);
    }
    /**
     * Compiles classes
     * @throws CompilationException
     * @throws IOException
     */
    public void compileClasses() throws CompilationException, IOException, ClassNotFoundException {

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
    public void loadClasses() throws MalformedURLException, ClassNotFoundException {
        for(JoopClass clazz : classes) {

            URL[] urls = new URL[] { new URL("file:" + config.getString(CONF_KEY_COMPILATION_DIR)) };
            //Create a new URLClassLoader with url to directory
            ClassLoader classLoader = new URLClassLoader(urls);

            //Load the class and return it
            Class loadedClass = classLoader.loadClass(clazz.getNameWithoutFileExtension());
            clazz.setLoadedClass(loadedClass);

        }
    }

    public List<JoopClass> getClasses() {
        return classes;
    }

    public List<JoopObject> getObjects() { return objects; }

    public void invokeConstructor(JoopClass clazz, String instanceName, Class[] parameters, Object[] arguments) {

        try {
            Constructor constructor = clazz.getLoadedClass().getConstructor(parameters);
            Object instance = constructor.newInstance(arguments);
            objects.add(new JoopObject(instanceName, instance));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

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

}
