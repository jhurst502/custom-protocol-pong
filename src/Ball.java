import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball extends Circle {

    private double radius = 12;
    private int x; // Dont hardcode screen size
    private int y;
    private double xDirection = -3.5; // -3.5 seems like a good speed to me, feel free to change
    private double yDirection = 3.5; // 3.5 eems like a good speed to me, feel free to change

    public Ball(int winX, int winY) {
        setRadius(radius);
        setFill(Color.WHITE);
        setCenterX(winX/2);
        setCenterY(winY/2);
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

    public double getxDirection() {
        return xDirection;
    }

    public void setxDirection(double xDirection) {
        this.xDirection = xDirection;
    }

    public double getyDirection() {
        return yDirection;
    }

    public void setyDirection(double yDirection) {
        this.yDirection = yDirection;
    }

    public void center(int winX, int winY) {
        this.x = winX/2; // Dont hardcode screen size
        this.y = winY/2;
    }
}
