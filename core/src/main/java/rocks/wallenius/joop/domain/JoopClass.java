package rocks.wallenius.joop.domain;

/**
 * Created by philipwallenius on 28/07/2019.
 */
public class JoopClass {
    private String fullyQualifiedName;
    private Class clazz;

    public JoopClass(String fullyQualifiedName, Class clazz) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.clazz = clazz;
    }
}
