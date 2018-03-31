package rocks.wallenius.joop.model.entity;

/**
 * Created by philipwallenius on 25/03/2018.
 */
public class JoopObject {

    private String fullyQualifiedClassName;
    private String instanceName;
    private Object object;

    public JoopObject(String fullyQualifiedClassName, String instanceName, Object object) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
        this.instanceName = instanceName;
        this.object = object;
    }

    public String getFullyQualifiedClassName() {
        return fullyQualifiedClassName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public Object getObject() {
        return object;
    }

}
