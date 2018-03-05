package rocks.wallenius.joop.gui.diagram;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import rocks.wallenius.joop.model.entity.JoopClass;

/**
 * Created by philipwallenius on 05/03/2018.
 */
public class UmlClass extends VBox {

    private JoopClass clazz;
    private StackPane titleBox;
    private Rectangle propertiesBox;
    private Rectangle methodsBox;
    private int x;
    private int y;
    private int width;
    private int height;

    private final static int DEFAULT_WIDTH = 140;
    private final static int DEFAULT_HEIGHT = 120;

    public UmlClass(int x, int y, JoopClass clazz) {
        super();
        this.x = x;
        this.y = y;
        this.clazz = clazz;

        setupShape();
        setupContent();
    }

    private void setupShape() {
        titleBox = new StackPane();
        Rectangle titleRectangle = new Rectangle();

        propertiesBox = new Rectangle();
        methodsBox = new Rectangle();

        titleRectangle.setWidth(DEFAULT_WIDTH);
        titleRectangle.setHeight(20);

        propertiesBox.setWidth(DEFAULT_WIDTH);
        propertiesBox.setHeight(50);
        methodsBox.setWidth(DEFAULT_WIDTH);
        methodsBox.setHeight(50);

        titleRectangle.setStroke(Color.BLACK);
        titleRectangle.setFill(Color.web("#FFFFF0"));

        propertiesBox.setStroke(Color.BLACK);
        propertiesBox.setFill(Color.web("#FFFFF0"));

        methodsBox.setStroke(Color.BLACK);
        methodsBox.setFill(Color.web("#FFFFF0"));

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.web("#888888"));
        setEffect(dropShadow);

        Text text = new Text();
        text.setText(clazz.getNameWithoutFileExtension());
        text.setFont(Font.font(null, FontWeight.BOLD, 18));
        titleBox.getChildren().addAll(titleRectangle, text);

        getChildren().addAll(titleBox, propertiesBox, methodsBox);
    }

    private void setupContent() {
        setupTitle();
    }

    private void setupTitle() {
//        StackPane stackPane = new StackPane();
//        Text text = new Text();
//        text.setText(clazz.getNameWithoutFileExtension());
//        text.setFont(Font.font(null, FontWeight.BOLD, 18));
//        stackPane.getChildren().addAll(text);
//        titleBox.getChildren().addAll(stackPane);
    }

}
