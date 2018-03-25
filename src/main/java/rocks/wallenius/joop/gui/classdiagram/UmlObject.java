package rocks.wallenius.joop.gui.classdiagram;

import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * Created by philipwallenius on 25/03/2018.
 */
public class UmlObject extends VBox {

    private final static int MIN_HEIGHT = 80;
    private final static int MIN_WIDTH = 100;

    private String instanceName;

    public UmlObject(int x, int y, String instanceName) {
        super();
        this.instanceName = instanceName;
        setLayoutX(x);
        setLayoutY(y);

        initializeShape();
    }

    private void initializeShape() {
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

        Stop[] stops = new Stop[] { new Stop(0, Color.web("#92cff6")), new Stop(1, Color.web("#85cbf8"))};
        LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        Background bg = new Background(new BackgroundFill(lg1, null, null));
        setBackground(bg);
    }

}
