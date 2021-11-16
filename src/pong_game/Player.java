package pong_game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;
import java.net.*;


public class Player extends Application {
    private boolean playing = true;
    public int windowX = 1200;
    public int windowY = 800;
    private int playerID;
    private int otherPlayer;

    private ClientSideConnection csc;

    // Client Connection Inner Class
    private class ClientSideConnection {

        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public ClientSideConnection() {
            System.out.println("----Client----");
            try {
                socket = new Socket("localhost", 51734); // exact same port as in PongServer Constructor
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                playerID = dataIn.readInt(); // first read in client
                System.out.println("Connected to server as Player #" + playerID + ".");
            } catch (IOException ex) {
                System.out.println("IOException from CSC constructor");
            }
        }
    }

    public void connectToServer() {
        csc = new ClientSideConnection();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Paddle p1 = new Paddle(1, windowX);
        Paddle p2 = new Paddle(2, windowX);
        Ball ball = new Ball(windowX, windowY);

        Pane pane = new Pane(p1, ball, p2);

        if (playerID == 1) {
            // Can possibly print something on the screen here saying "you are player 1" or something
            otherPlayer = 2;
        } else {
            // and then here something saying "you are player 2"
            otherPlayer = 1;
        }

        Player p = new Player();
        p.connectToServer();

        p1.setOnKeyPressed(e -> {
//            switch (e.getCode()) {
//                case UP -> p1.moveUp();
//                case DOWN -> p1.moveDown(windowY);
//            }
            if (e.getCode() == KeyCode.UP) {
                p1.moveUp();
            }
            else if (e.getCode() == KeyCode.DOWN) {
                p1.moveDown(windowY);
            }
        });

        p1.setOnKeyReleased(e -> {
            p1.stopMovement();
        });
        pane.setStyle("-fx-background-color: black;");
        pane.setPrefSize(windowX, windowY);
        primaryStage.setTitle("Player #" + playerID + "Networked Pong");
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();

        p1.requestFocus();

        primaryStage.setOnCloseRequest(e -> Platform.exit());

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

    public void score(Paddle paddle) {
        paddle.score += 1;
        System.out.println("Player #" + paddle.playerID + " score: " + paddle.score);
    }
}
