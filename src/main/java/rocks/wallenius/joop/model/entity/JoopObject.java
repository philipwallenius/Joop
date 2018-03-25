package rocks.wallenius.joop.model.entity;

/**
 * Created by philipwallenius on 25/03/2018.
 */
public class JoopObject {

    private String instanceName;
    private Object object;

    public JoopObject(String instanceName, Object object) {
        this.instanceName = instanceName;
        this.object = object;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public Object getObject() {
        return object;
    }

}
