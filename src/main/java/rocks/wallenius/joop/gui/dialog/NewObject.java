package rocks.wallenius.joop.gui.dialog;

import java.util.Map;

/**
 * Created by philipwallenius on 25/03/2018.
 */
public class NewObject {

    private String instanceName;
    private Map<Class, Object> arguments;

    public NewObject(String instanceName, Map<Class, Object> arguments) {
        this.instanceName = instanceName;
        this.arguments = arguments;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public Map<Class, Object> getArguments() {
        return arguments;
    }

}
