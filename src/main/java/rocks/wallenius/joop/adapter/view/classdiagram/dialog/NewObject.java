package rocks.wallenius.joop.adapter.view.classdiagram.dialog;

import java.util.List;

/**
 * Created by philipwallenius on 25/03/2018.
 */
public class NewObject {

    private String instanceName;
    private List<Class> parameters;
    private List<Object> arguments;

    public NewObject(String instanceName, List<Class> parameters, List<Object> arguments) {
        this.instanceName = instanceName;
        this.parameters = parameters;
        this.arguments = arguments;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public List<Class> getParameters() {
        return parameters;
    }

    public List<Object> getArguments() {
        return arguments;
    }

}
