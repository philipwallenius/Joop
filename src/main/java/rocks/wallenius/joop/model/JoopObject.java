package rocks.wallenius.joop.model;

/**
 * Created by philipwallenius on 25/03/2018.
 */
public class JoopObject {

    private String fullyQualifiedClassName;
    private String reference;
    private Object object;

    public JoopObject(String fullyQualifiedClassName, String reference, Object object) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
        this.reference = reference;
        this.object = object;
    }

    public String getFullyQualifiedClassName() {
        return fullyQualifiedClassName;
    }

    public String getReference() {
        return reference;
    }

    public Object getObject() {
        return object;
    }

}
