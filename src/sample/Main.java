package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;


public class Main extends Application {
    private boolean playing = true;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Paddle p1 = new Paddle(1);
        Paddle p2 = new Paddle(2);
        Ball ball = new Ball();

        Pane pane = new Pane(p1, ball, p2);

        p1.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP: p1.moveUp(); break;
                case DOWN: p1.moveDown(); break;
            }
        });

        p1.setOnKeyReleased(e -> {
            p1.stopMovement();
        });
        pane.setStyle("-fx-background-color: black;");
        pane.setPrefSize(600, 400);
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
            int yDirection = ball.getyDirection();
            int xDirection = ball.getxDirection();
            int x = ball.getX();
            int y = ball.getY();
            if (ball.getCenterY() < 0 || ball.getCenterY() > 300) {
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
                ball.setX(x + xDirection);
            }

            // If the ball goes out of bounds we need to reset and assign points to the correct player
            if (ball.getX() < 0) {
                score(p2);
                playing = false;
            }
            else if (ball.getX() > 600) {
                score(p1);
                playing = false;
            }
        }
        else if (!playing) {
            // Reposition the ball and start a countdown until ball is live
            ball.center();
            TimerTask play = new TimerTask() {
                @Override
                public void run() {
                    playing = true;
                }
            };
            Timer timer = new Timer();
            timer.schedule(play, 3000);
        }

    }

    public void score(Paddle player) {
        System.out.println(player.score);
        player.score += 1;
    }
}
