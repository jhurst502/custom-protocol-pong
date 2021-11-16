package pong_game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;


public class Player extends Application {
    private boolean playing = true;
    public int windowX = 1200;
    public int windowY = 800;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Paddle p1 = new Paddle(1, windowX);
        Paddle p2 = new Paddle(2, windowX);
        Ball ball = new Ball(windowX, windowY);

        Pane pane = new Pane(p1, ball, p2);

        p1.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP -> p1.moveUp();
                case DOWN -> p1.moveDown(windowY);
            }
        });

        p1.setOnKeyReleased(e -> {
            p1.stopMovement();
        });
        pane.setStyle("-fx-background-color: black;");
        pane.setPrefSize(windowX, windowY);
        primaryStage.setTitle("Peer to Peer Pong");
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();

        p1.requestFocus();

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(10),
                e -> {
                    try {
                        moveBall(ball, p1, p2);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void moveBall(Ball ball, Paddle p1, Paddle p2) throws Exception {
        if (playing) {
            double yDirection = ball.getyDirection();
            double xDirection = ball.getxDirection();
            int x = ball.getX();
            int y = ball.getY();
            if (ball.getCenterY() < 0 || ball.getCenterY() > windowY) {
                ball.setyDirection(yDirection *= -1);
            }

            x += xDirection;
            y += yDirection;

            ball.setX(x);
            ball.setY(y);

            ball.setCenterX(ball.getX());
            ball.setCenterY(ball.getY());

            if (ball.getLayoutBounds().intersects(p1.getLayoutBounds()) || ball.getLayoutBounds().intersects(p2.getLayoutBounds())) {
                ball.setxDirection(xDirection *= -1);
                ball.setX(x + (int)Math.ceil(xDirection));
            }

            // If the ball goes out of bounds we need to reset and assign points to the correct player
            if (ball.getX() < 0) {
                score(p2);
                playing = false;
            }
            else if (ball.getX() > windowX) {
                score(p1);
                playing = false;
            }
        }
        else {
            // Reposition the ball and start a countdown until ball is live
            ball.center(windowX, windowY);
            TimerTask play = new TimerTask() {
                @Override
                public void run() {
                    playing = true;
                }
            };
            Timer timer = new Timer();
            timer.schedule(play, 250);
        }

    }

    public void score(Paddle player) {
        player.score += 1;
        System.out.println("Player #" + player.playerID + " score: " + player.score);
    }
}
