package rocks.wallenius.joop.model.entity;

import java.nio.file.Path;

/**
 * Created by philipwallenius on 04/03/2018.
 */
public class JoopClass {

    private String fullyQualifiedName;
    private Path path;
    private String definition;
    private Class loadedClass;

    public JoopClass(String fullyQualifiedName, Path path) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.path = path;
        this.definition = "";
    }

    public String getName() {
        return path.getFileName().toString();
    }

    public String getNameWithoutFileExtension() {
        String name = path.getFileName().toString();
        return name.substring(0, name.lastIndexOf('.'));
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Path getPath() {
        return path;
    }

    public Class getLoadedClass() {
        return loadedClass;
    }

    public void setLoadedClass(Class loadedClass) {
        this.loadedClass = loadedClass;
    }

}
