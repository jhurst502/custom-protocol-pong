package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Ball extends Circle {

    private double radius = 12;
    private int x = 600/2; // Dont hardcode screen size
    private int y = 350/2;
    private int xDirection = -1;
    private int yDirection = 1;

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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getxDirection() {
        return xDirection;
    }

    public void setxDirection(int xDirection) {
        this.xDirection = xDirection;
    }

    public int getyDirection() {
        return yDirection;
    }

    public void setyDirection(int yDirection) {
        this.yDirection = yDirection;
    }

    private void moveBall() {
        if (getCenterY() < 0 || getCenterY() > 300) {
            yDirection *= -1;
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