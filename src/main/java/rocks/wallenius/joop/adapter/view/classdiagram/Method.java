package rocks.wallenius.joop.adapter.view.classdiagram;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class Method {

    private String name;
    private String returnType;
    private String accessModifier;
    private Parameter[] parameters;
    private boolean isStatic;
    private boolean isFinal;

    public Method(String name, String returnType, String accessModifier, Parameter[] parameters, boolean isStatic, boolean isFinal) {
        this.name = name;
        this.returnType = returnType;
        this.accessModifier = accessModifier;
        this.parameters = parameters;
        this.isStatic = isStatic;
        this.isFinal = isFinal;
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getAccessModifier() {
        return accessModifier;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isFinal() {
        return isFinal;
    }

}
