package rocks.wallenius.joop.adapter.view.editor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.wellbehaved.event.Nodes;
import rocks.wallenius.joop.adapter.model.Source;

import java.util.function.IntFunction;

import static javafx.scene.input.KeyCode.TAB;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;
import static org.fxmisc.wellbehaved.event.InputMap.consume;

/**
 * Created by philipwallenius on 15/02/16.
 */
public class Tab extends javafx.scene.control.Tab {

    private CodeArea codeArea;
    private Source source;

    public Tab(Source source) {
        super(source.getClassName());
        this.source = source;

        codeArea = new CodeArea();

        // Update source object when code is edited
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(s -> source.setCode(codeArea.getText()));

        setupCodeArea(codeArea);
        setupKeyBehaviour(codeArea);

        codeArea.appendText(source.getCode());
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

    public Source getSource() {
        return source;
    }

}
