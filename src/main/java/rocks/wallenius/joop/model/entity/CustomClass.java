package rocks.wallenius.joop.model.entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.fxmisc.richtext.CodeArea;

import java.nio.file.Path;

/**
 * Created by philipwallenius on 15/02/16.
 */
public class CustomClass {

    private String name;
    private String code;
    private Path path;
    private BooleanProperty changed;

    public CustomClass() {
        changed = new SimpleBooleanProperty(false);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public boolean getChanged() {
        return changed.get();
    }

    public BooleanProperty changedProperty() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed.set(changed);
    }
}
