package rocks.wallenius.joop.model;

import rocks.wallenius.joop.model.entity.Clazz;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipwallenius on 14/02/16.
 */
public class Model {

    private List<Clazz> classes;

    public Model() {
        classes = new ArrayList<Clazz>();
    }

    public List<Clazz> getClasses() {
        return classes;
    }

    public void setClasses(List<Clazz> classes) {
        this.classes = classes;
    }

    public void addClass(Clazz clazz) {
        classes.add(clazz);
    }

    public void removeClass(Clazz clazz) {
        classes.remove(clazz);
    }

}
