import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Paddle extends Rectangle {

    public int score;
    private int x;
    private int y;
    public int playerID;

    public Paddle(int playerNumber, int winX) {
        setHeight(80);
        setWidth(15);
        setFill(Color.WHITE);
        playerID = playerNumber;

        if (playerNumber == 1) {
            setX(0);
        } else if (playerNumber == 2) {
            setX(winX - getWidth());
        }
    }

    public void moveUp() {
        setY(getY() - 20); //don't hardcode stage height
        if (getY() < 0) {
            setY(0);
        }
    }

    public void moveDown(int winY) {
        setY(getY() + 20);
        if (getY() + getHeight() > winY) {
            setY(winY - getHeight());
        }
    }

    public void stopMovement() {}
}
