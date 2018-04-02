package rocks.wallenius.joop.gui.objectdiagram;

import rocks.wallenius.joop.gui.classdiagram.Parameter;

/**
 * Created by philipwallenius on 02/04/2018.
 */
public class Method {

    private String name;
    private String returnType;
    private Parameter[] parameters;

    public Method(String name, String returnType, Parameter[] parameters) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

}
