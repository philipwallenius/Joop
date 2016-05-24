package rocks.wallenius.joop.model.entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by philipwallenius on 15/02/16.
 */
public class CustomClass {

    private CodeArea codeArea;
    private Path path;
    private BooleanProperty changed;
    private Class loadedClass;

    public CustomClass() {
        codeArea = new CodeArea();
        changed = new SimpleBooleanProperty(false);
    }

    public String getName() {
        return path.getFileName().toString();
    }

    public String getNameWithoutFileExtension() {
        String name = path.getFileName().toString();
        return name.substring(0, name.lastIndexOf('.'));
    }

    public CodeArea getCodeArea() {
        return codeArea;
    }

    public void setCodeArea(CodeArea codeArea) {
        this.codeArea = codeArea;
    }

    public String getCode() {
        return codeArea.getText();
    }

    public void setCode(String code) {
        codeArea.clear();
        codeArea.appendText(code);
    }

    public File getFile() {
        return path.toFile();
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

    public Class getLoadedClass() {
        return loadedClass;
    }

    public void setLoadedClass(Class loadedClass) {
        this.loadedClass = loadedClass;
    }

}
