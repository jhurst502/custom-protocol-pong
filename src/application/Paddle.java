import javafx.animation.TranslateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Paddle extends Rectangle {

    public int score;
    public int playerID;
    public boolean stopped = true;

    public Paddle(int playerNumber, int winX) {
        setHeight(80);
        setWidth(15);
        setFill(Color.WHITE);
        playerID = playerNumber;
        setY(-1);

        if (playerNumber == 1) {
            setX(0);
        } else if (playerNumber == 2) {
            setX(winX - getWidth());
        }
    }

    public void moveUp() {
        this.stopped = false;
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(this);
        translate.setByY(-30);
        translate.play();
        setY(getY() - 30); //don't hardcode stage height
        if (getY() < 0) {
            setY(0);
        }
    }

    public void moveDown(int winY) {
        this.stopped = false;
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(this);
        translate.setByY(30);
        translate.play();
        setY(getY() + 30);
        if (getY() + getHeight() > winY) {
            setY(winY - getHeight());
        }
    }

    public void stopMovement() {
        this.stopped = true;
    }
}
