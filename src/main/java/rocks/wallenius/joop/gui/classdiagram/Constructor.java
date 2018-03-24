package rocks.wallenius.joop.gui.classdiagram;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class Constructor {

    private String name;
    private String accessModifier;

    public Constructor(String name, String accessModifier) {
        this.name = name;
        this.accessModifier = accessModifier;
    }

    public String getName() {
        return name;
    }

    public String getAccessModifier() {
        return accessModifier;
    }

}
