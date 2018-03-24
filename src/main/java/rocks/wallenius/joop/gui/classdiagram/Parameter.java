package rocks.wallenius.joop.gui.classdiagram;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class Parameter {

    private String name;
    private String type;

    public Parameter(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

}
