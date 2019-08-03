package rocks.wallenius.joop.view.classdiagram;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class Constructor {

    private String name;
    private String accessModifier;
    private Parameter[] parameters;

    public Constructor(String name, String accessModifier, Parameter[] parameters) {
        this.name = name;
        this.accessModifier = accessModifier;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public String getAccessModifier() {
        return accessModifier;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

}
