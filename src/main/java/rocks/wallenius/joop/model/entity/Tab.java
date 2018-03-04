package rocks.wallenius.joop.model.entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.fxmisc.richtext.CodeArea;

/**
 * Created by philipwallenius on 15/02/16.
 */
public class Tab extends javafx.scene.control.Tab {

    private CodeArea codeArea;
    private BooleanProperty changed;
    private String clazz;

    public Tab(JoopClass clazz) {
        super(clazz.getName());
        this.clazz = clazz.getFullyQualifiedName();
        codeArea = new CodeArea();
        codeArea.appendText(clazz.getDefinition());
        setContent(codeArea);
        changed = new SimpleBooleanProperty(false);
    }

    public CodeArea getCodeArea() {
        return codeArea;
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

    public String getClazz() {
        return clazz;
    }



}
