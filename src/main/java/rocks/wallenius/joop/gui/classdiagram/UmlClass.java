package rocks.wallenius.joop.gui.classdiagram;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipwallenius on 05/03/2018.
 */
public class UmlClass extends VBox {

    private StackPane titleBox;
    private StackPane propertiesBox;
    private StackPane methodsBox;
    private StackPane constructorsBox;

    private final static int MIN_HEIGHT = 80;
    private final static int MIN_WIDTH = 100;

    private String title;
    private Field[] fields;
    private Constructor[] constructors;
    private Method[] methods;

    public UmlClass(int x, int y, String title, Field[] fields, Constructor[] constructors, Method[] methods) {
        super();

        this.title = title;
        this.fields = fields;
        this.constructors = constructors;
        this.methods = methods;

        setLayoutX(x);
        setLayoutY(y);

        initializeShape();
        initializeContent();

    }

    private void initializeShape() {
        titleBox = new StackPane();
        propertiesBox = new StackPane();
        methodsBox = new StackPane();
        constructorsBox = new StackPane();

        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
        setPadding(new Insets(10, 10, 10, 10));
        setMinWidth(MIN_WIDTH);
        setMinHeight(MIN_HEIGHT);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.web("#888888"));
        setEffect(dropShadow);

        Stop[] stops = new Stop[] { new Stop(0, Color.web("#ff7075")), new Stop(1, Color.web("#ff6f74"))};
        LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        Background bg = new Background(new BackgroundFill(lg1, null, null));
        setBackground(bg);

        getChildren().addAll(titleBox, propertiesBox, constructorsBox, methodsBox);
    }

    private void initializeContent() {
        drawTitle();
        drawFields();
        drawConstructors();
        drawMethods();
    }

    private void drawTitle() {

        VBox titleHolder = new VBox();
        titleHolder.setAlignment(Pos.BASELINE_CENTER);
        titleHolder.setPadding(new Insets(0, 0, 10, 0));

        Text text = new Text();
        text.setText(title);
        text.setFont(Font.font(null, FontWeight.BOLD, 14));

        titleHolder.getChildren().addAll(text);

        titleBox.getChildren().addAll(titleHolder);
    }

    private void drawFields() {
        VBox propertiesHolder = new VBox();
        propertiesHolder.setAlignment(Pos.BASELINE_LEFT);
        propertiesHolder.setPadding(new Insets(0, 0, 10, 0));



        List<Text> props = new ArrayList<Text>();

        for(Field field : fields) {
            Text text = new Text();
            text.setText(String.format("%s %s : %s", getAccessModifierSymbol(field.getAccessModifier()), field.isFinal() ? field.getName().toUpperCase() : field.getName(), field.getType()));
            text.setFont(Font.font(null, FontPosture.REGULAR, 10));
            if(field.isStatic()) {
                text.setUnderline(true);
            }
            props.add(text);
        }

        for(Text prop : props) {
            propertiesHolder.getChildren().addAll(prop);
        }

        propertiesBox.getChildren().addAll(propertiesHolder);
    }

    private void drawConstructors() {
        VBox constructorsHolder = new VBox();
        constructorsHolder.setAlignment(Pos.BASELINE_LEFT);
        constructorsHolder.setPadding(new Insets(0, 0, 10, 0));


        List<Text> c = new ArrayList<Text>();

        for(Constructor constructor : constructors) {
            Text text = new Text();
            text.setText(String.format("%s %s()", getAccessModifierSymbol(constructor.getAccessModifier()), constructor.getName()));
            text.setFont(Font.font(null, FontWeight.BOLD, 10));
            c.add(text);
        }

        for(Text prop : c) {
            constructorsHolder.getChildren().addAll(prop);
        }

        constructorsBox.getChildren().addAll(constructorsHolder);
    }

    private void drawMethods() {
        VBox methodsHolder = new VBox();
        methodsHolder.setAlignment(Pos.BASELINE_LEFT);
        methodsHolder.setPadding(new Insets(0, 0, 0, 0));


        List<Text> m = new ArrayList<Text>();

        for(Method method : methods) {
            Text text = new Text();
            text.setText(String.format("%s %s() : %s", getAccessModifierSymbol(method.getAccessModifier()), method.getName(), method.getReturnType()));
            text.setFont(Font.font(null, FontWeight.BOLD, 10));
            if(method.isStatic()) {
                text.setUnderline(true);
            }
            m.add(text);
        }

        for(Text prop : m) {
            methodsHolder.getChildren().addAll(prop);
        }

        methodsBox.getChildren().addAll(methodsHolder);
    }

    private static String getAccessModifierSymbol(String accessModifier) {
        switch (accessModifier) {
            case "public" : {
                return "+";
            }
            case "private" : {
                return "-";
            }
            case "protected" : {
                return "#";
            }
            case "package" : {
                return "~";
            }
            default: {
                throw new IllegalArgumentException(String.format("Invalid accessModifier: %", accessModifier));
            }
        }
    }

}
