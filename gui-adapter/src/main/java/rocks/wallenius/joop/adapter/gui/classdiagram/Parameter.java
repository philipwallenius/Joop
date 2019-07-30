package rocks.wallenius.joop.adapter.gui.classdiagram;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class Parameter {

    private String name;
    private Class type;

    public Parameter(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    public String getTypeSimpleName() {
        return type.getSimpleName();
    }

}
