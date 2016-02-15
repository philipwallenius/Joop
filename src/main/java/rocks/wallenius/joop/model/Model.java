package rocks.wallenius.joop.model;

import rocks.wallenius.joop.model.entity.CustomClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipwallenius on 14/02/16.
 */
public class Model {

    private List<CustomClass> classes;

    public Model() {
        classes = new ArrayList<CustomClass>();
    }

    public List<CustomClass> getClasses() {
        return classes;
    }

    public void setClasses(List<CustomClass> classes) {
        this.classes = classes;
    }

    public void addClass(CustomClass customClass) {
        classes.add(customClass);
    }

    public void removeClass(CustomClass customClass) {
        classes.remove(customClass);
    }

}
