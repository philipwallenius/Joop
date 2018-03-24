package rocks.wallenius.joop.gui.classdiagram;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class Method {

    private String name;
    private String returnType;
    private String accessModifier;
    private boolean isStatic;
    private boolean isFinal;

    public Method(String name, String returnType, String accessModifier, boolean isStatic, boolean isFinal) {
        this.name = name;
        this.returnType = returnType;
        this.accessModifier = accessModifier;
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

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isFinal() {
        return isFinal;
    }

}
