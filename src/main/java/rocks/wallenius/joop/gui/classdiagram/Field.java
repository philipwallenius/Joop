package rocks.wallenius.joop.gui.classdiagram;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class Field {

    private String name;
    private String type;
    private String accessModifier;
    private boolean isStatic;
    private boolean isFinal;

    public Field(String name, String type, String accessModifier, boolean isStatic, boolean isFinal) {
        this.name = name;
        this.type = type;
        this.accessModifier = accessModifier;
        this.isStatic = isStatic;
        this.isFinal = isFinal;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
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
