package src.Pong;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Ball extends Circle {

    private double radius = 15;
    private int x = 600/2; // Dont hardcode screen size
    private int y = 350/2;
    private int xDirection;
    private int yDirection;

    public Ball() {
        setRadius(radius);
        setFill(Color.WHITE);
        setCenterX(x);
        setCenterY(y);
    }

    public void startGame() {
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(50),
                e -> moveBall()));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    public void hit() {
        xDirection *= -1;
    }

    private void moveBall() {
        if (getCenterY() < 0 || getCenterY() > 300) {
            yDirection *= -1;
        } else {
            yDirection += 1;
        }

        xDirection -= 1;

        x += xDirection;
        y += yDirection;

        if (getCenterX() < 0) {
            x = 600;
        }
        setCenterX(x);
        setCenterY(y);
    }


}
