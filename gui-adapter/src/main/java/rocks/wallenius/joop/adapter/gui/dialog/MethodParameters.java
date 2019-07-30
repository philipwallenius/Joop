package rocks.wallenius.joop.adapter.gui.dialog;

import java.util.List;

/**
 * Created by philipwallenius on 02/04/2018.
 */
public class MethodParameters {

    private List<Class> parameters;
    private List<Object> arguments;

    public MethodParameters(List<Class> parameters, List<Object> arguments) {
        this.parameters = parameters;
        this.arguments = arguments;
    }

    public List<Class> getParameters() {
        return parameters;
    }

    public List<Object> getArguments() {
        return arguments;
    }

}
