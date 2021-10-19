package sample;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Paddle extends Rectangle {

    private int x;
    private int y;

    public Paddle(int playerNumber) {
        setHeight(40);
        setWidth(15);
        setFill(Color.WHITE);

        if (playerNumber == 1) {
            setX(0);
        } else if (playerNumber == 2) {
            setX(600 - getWidth());
        }
    }

    public void moveUp() {
        setY(getY() - 350 /35); //don't hardcode stage height
        if (getY() < 0) {
            setY(0);
        }
    }

    public void moveDown() {
        setY(getY() + 350 /35);
        if (getY() + getHeight() > 350) {
            setY(350 - getHeight());
        }
    }
}
