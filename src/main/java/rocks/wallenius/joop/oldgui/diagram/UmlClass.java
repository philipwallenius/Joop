package rocks.wallenius.joop.oldgui.diagram;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import rocks.wallenius.joop.model.entity.JoopClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipwallenius on 05/03/2018.
 */
public class UmlClass extends VBox {

    private JoopClass clazz;
    private StackPane titleBox;
    private StackPane propertiesBox;
    private StackPane methodsBox;

    private final static int DEFAULT_WIDTH = 140;
    private final static int DEFAULT_HEIGHT = 120;

    public UmlClass(int x, int y, JoopClass clazz) {
        super();
        setLayoutX(x);
        setLayoutY(y);
        this.clazz = clazz;

        setupShape();
        setupContent();
    }

    private void setupShape() {
        titleBox = new StackPane();


        propertiesBox = new StackPane();
        methodsBox = new StackPane();

        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.web("#888888"));
        setEffect(dropShadow);

        getChildren().addAll(titleBox, propertiesBox, methodsBox);
    }

    private void setupContent() {
        setupTitle();
        setupProperties();
        setupMethods();
    }

    private void setupTitle() {
        Rectangle titleRectangle = new Rectangle();
        titleRectangle.setWidth(DEFAULT_WIDTH);
        titleRectangle.setHeight(20);
        titleRectangle.setStroke(Color.BLACK);
        titleRectangle.setFill(Color.web("#FFFFF0"));

        Text text = new Text();
        text.setText(clazz.getNameWithoutFileExtension());
        text.setFont(Font.font(null, FontWeight.BOLD, 14));
        titleBox.getChildren().addAll(titleRectangle, text);
    }

    private void setupProperties() {
        VBox propertiesHolder = new VBox();
        propertiesHolder.setAlignment(Pos.BASELINE_LEFT);
        propertiesHolder.setPadding(new Insets(5, 0, 0, 10));
        Rectangle titleRectangle = new Rectangle();
        titleRectangle.setWidth(DEFAULT_WIDTH);
        titleRectangle.setHeight(50);
        titleRectangle.setStroke(Color.BLACK);
        titleRectangle.setFill(Color.web("#FFFFF0"));

        Field[] fields = clazz.getLoadedClass().getDeclaredFields();

        List<Text> props = new ArrayList<Text>();

        for(Field field : fields) {
            Text text = new Text();
            text.setText(field.getName());
            text.setFont(Font.font(null, FontWeight.BOLD, 10));
            props.add(text);
        }

        propertiesBox.getChildren().addAll(titleRectangle);

        for(Text prop : props) {
            propertiesHolder.getChildren().addAll(prop);
        }

        propertiesBox.getChildren().addAll(propertiesHolder);
    }

    private void setupMethods() {
        VBox methodsHolder = new VBox();
        methodsHolder.setAlignment(Pos.BASELINE_LEFT);
        methodsHolder.setPadding(new Insets(5, 0, 0, 10));
        Rectangle methodsRectangle = new Rectangle();
        methodsRectangle.setWidth(DEFAULT_WIDTH);
        methodsRectangle.setHeight(50);
        methodsRectangle.setStroke(Color.BLACK);
        methodsRectangle.setFill(Color.web("#FFFFF0"));

        Method[] methods = clazz.getLoadedClass().getDeclaredMethods();

        List<Text> m = new ArrayList<Text>();

        for(Method method : methods) {
            Text text = new Text();
            text.setText(method.getName());
            text.setFont(Font.font(null, FontWeight.BOLD, 10));
            m.add(text);
        }

        methodsBox.getChildren().addAll(methodsRectangle);

        for(Text prop : m) {
            methodsHolder.getChildren().addAll(prop);
        }

        methodsBox.getChildren().addAll(methodsHolder);
    }

}
