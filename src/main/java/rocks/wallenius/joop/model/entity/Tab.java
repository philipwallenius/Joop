package rocks.wallenius.joop.model.entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.wellbehaved.event.Nodes;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.scene.input.KeyCode.TAB;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;
import static org.fxmisc.wellbehaved.event.InputMap.consume;

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
        changed = new SimpleBooleanProperty(false);

        setupCodeArea(codeArea);
        setupKeyBehaviour(codeArea);

        codeArea.appendText(clazz.getDefinition());
        setContent(codeArea);
    }

    private void setupCodeArea(CodeArea codeArea) {
        IntFunction<Node> numberFactory = LineNumberFactory.get(codeArea);
        IntFunction<Node> graphicFactory = line -> {
            HBox hbox = new HBox(
                    numberFactory.apply(line));
            hbox.setAlignment(Pos.CENTER_LEFT);
            return hbox;
        };
        codeArea.setParagraphGraphicFactory(graphicFactory);
    }

    private void setupKeyBehaviour(CodeArea codeArea) {
        Nodes.addInputMap(codeArea, consume(keyPressed(TAB), e -> codeArea.replaceSelection("    ")));
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
