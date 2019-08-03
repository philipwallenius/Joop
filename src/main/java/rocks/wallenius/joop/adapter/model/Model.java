package rocks.wallenius.joop.adapter.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipwallenius on 03/08/2019.
 */
public class Model {

    private List<Source> sources;
    private List<Class> classes;
    private List<JoopObject> objects;

    public Model() {
        sources = new ArrayList<>();
        classes = new ArrayList<>();
        objects = new ArrayList<>();
    }

    public void addSource(Source source) {
        if(!sources.contains(source)) {
            this.sources.add(source);
        }
    }

    public void removeSource(Source source) {
        this.sources.remove(source);
    }

    public List<Source> getSources() { return sources; }

    public List<Class> getClasses() {
        return classes;
    }

    public List<JoopObject> getObjects() {
        return objects;
    }

    public void clearClasses() {
        this.classes.clear();
    }

    public void clearObjects() {
        this.objects.clear();
    }

    public void addClass(Class newClass) {
        this.classes.add(newClass);
    }

    public void addObject(JoopObject object) {
        this.objects.add(object);
    }

}
