package rocks.wallenius.joop.view.objectdiagram;

/**
 * Created by philipwallenius on 31/03/2018.
 */
public class Property {

    private String name;
    private Object value;

    public Property(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

}
