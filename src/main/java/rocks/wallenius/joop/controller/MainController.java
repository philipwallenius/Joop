package rocks.wallenius.joop.controller;

import rocks.wallenius.joop.compiler.CompilationException;
import rocks.wallenius.joop.compiler.CompilerUtil;
import rocks.wallenius.joop.configuration.ConfigurationService;
import rocks.wallenius.joop.model.Model;
import rocks.wallenius.joop.model.entity.CustomClass;
import rocks.wallenius.joop.util.ClassFileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private Model model;
    private static ConfigurationService config;

    MainController(Model model) {
        this.model = model;
        config = ConfigurationService.getInstance();
    }

    /**
     * Creates a new class and opens a new tab with a CodeArea
     *
     * @param pathToNewClass of new class
     * @throws IOException
     * @throws URISyntaxException
     */
    CustomClass createNewClass(String pathToNewClass) throws IOException, URISyntaxException {

        CustomClass newCustomClass = new CustomClass();

        // sets path and prepends directory path to it
        newCustomClass.setPath(new File(String.format("%s%s.java", config.getString(CONF_KEY_SOURCES_DIR), pathToNewClass)).toPath());

        // check if path of class contains package definitions and add template code to the class
        if (containsPackageDefintions(pathToNewClass)) {
            String className = newCustomClass.getName().substring(0, newCustomClass.getName().lastIndexOf(".java"));
            String pckg = pathToNewClass.substring(0, pathToNewClass.lastIndexOf("."));
            newCustomClass.setCode(loadClassTemplate(className, pckg));
        } else {
            newCustomClass.setCode(loadClassTemplate(newCustomClass.getName().substring(0, newCustomClass.getName().lastIndexOf(".java"))));
        }

        // save new class to file
        saveClass(newCustomClass);
        model.addClass(newCustomClass);

        return newCustomClass;
    }

    /**
     * Opens a class from file
     * @param file to open class from
     * @throws IOException
     */
    CustomClass openClass(File file) throws IOException {
        CustomClass loadedCustomClass;

        String code = ClassFileUtils.loadClass(file);

        loadedCustomClass = new CustomClass();
        loadedCustomClass.setCode(code);
        loadedCustomClass.setPath(file.toPath());

        model.addClass(loadedCustomClass);

        return loadedCustomClass;
    }

    /**
     * Saves a class to file
     *
     * @param clazz to save to file
     * @throws IOException
     */
    void saveClass(CustomClass clazz) throws IOException {
        ClassFileUtils.saveClass(clazz);
        clazz.setChanged(false);
    }

    /**
     * Checks if fully qualified classname String contains package defintions
     *
     * @param className fully qualified classname
     * @return Returns true or false
     */
    private boolean containsPackageDefintions(String className) {
        Matcher matcher = Pattern.compile("[.]{1}").matcher(className);
        return matcher.find();
    }

    /**
     * Loads the default class code from a template and inserts the specific class name
     *
     * @param className to insert into template class code
     * @return Returns template class code with passed class name
     * @throws IOException
     * @throws URISyntaxException
     */
    private String loadClassTemplate(String className) throws IOException, URISyntaxException {
        URL url = this.getClass().getResource("/class_template.java");
        String content = new String(Files.readAllBytes(Paths.get(url.toURI())));
        return content.replace("<<CLASSNAME>>", className).replace("<<PACKAGE>>\n\n", "");
    }

    /**
     * Loads the default class code from template and inserts the specific class name and package
     *
     * @param className to insert into template class code
     * @param pckg      is the package to insert into template class code
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private String loadClassTemplate(String className, String pckg) throws IOException, URISyntaxException {
        URL url = this.getClass().getResource("/class_template.java");
        String content = new String(Files.readAllBytes(Paths.get(url.toURI())));
        return content.replace("<<CLASSNAME>>", className).replace("<<PACKAGE>>", String.format("package %s;", pckg));
    }

    /**
     * Compiles all open classes
     * @throws CompilationException
     * @throws IOException
     */
    void compileClasses() throws CompilationException, IOException, ClassNotFoundException {

        // save all classes
        for(CustomClass customClass : model.getClasses()) {
            saveClass(customClass);
        }

        // compile all open classes
        if (model.getClasses().size() > 0) {
            List<File> fileList = model.getClasses().stream().map(CustomClass::getFile).collect(Collectors.toList());
            CompilerUtil.compile(fileList.toArray(new File[fileList.size()]));
        }

    }

    /**
     * Loads compiled classes dynamically
     */
    void loadClasses() throws MalformedURLException, ClassNotFoundException {
        for(CustomClass customClass : model.getClasses()) {

            URL[] urls = new URL[] { new URL("file:" + config.getString(CONF_KEY_COMPILATION_DIR)) };
            //Create a new URLClassLoader with url to directory
            ClassLoader classLoader = new URLClassLoader(urls);

            //Load the class and return it
            Class loadedClass = classLoader.loadClass(customClass.getNameWithoutFileExtension());
            customClass.setLoadedClass(loadedClass);

        }
    }

}
