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
    private boolean playing = false;
    public int windowX = 1200;
    public int windowY = 800;
    private int playerID;
    private int otherPlayer;
    private int myScore = 0;
    private int enemyScore = 0;

    private ClientSideConnection csc;

    public void startReceivingPaddlePos(Paddle otherPaddle) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    csc.receivePaddlePos(otherPaddle);
                }
            }
        });
        t.start();
    }

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

        // method to send this player's paddle position to the server
        public void sendPaddlePos(int n) {
            try {
                dataOut.writeInt(n);
                dataOut.flush();;
            } catch (IOException ex) {
                System.out.println("IOException from sendPaddlePos() CSC");
            }
        }

        // method to receive the other player's paddle position from the server
        public int receivePaddlePos(Paddle otherPaddle) {
            int n = -1;
            try {
                n = dataIn.readInt();
                //System.out.println("Paddle " + otherPlayer + ": Y" + n); // debug
                otherPaddle.setY(n);
            } catch (IOException ex) {
                System.out.println("IOExcetpion from receivePaddlePos() CSC");
            }
            return n;
        }
    }

    public void connectToServer() {
        csc = new ClientSideConnection();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Paddle p1 = new Paddle(1, windowX);
        Paddle p2 = new Paddle(2, windowX);
        Ball ball = new Ball(windowX, windowY);

        Pane pane = new Pane(p1, ball, p2);

        Player p = new Player();
        p.connectToServer();

        if (p.playerID == 1) {
            // Can possibly print something on the screen here saying "you are player 1" or something
            p.otherPlayer = 2;
            // Move the other player's paddle when the other player moves it
            p.startReceivingPaddlePos(p2);
        } else {
            // and then here something saying "you are player 2"
            p.otherPlayer = 1;
            // Move the other player's paddle when the other player moves it
            p.startReceivingPaddlePos(p1);
        }

        // Paddle movement
        p1.setOnKeyPressed(e -> {
            if (p.playerID == 1) {
                if (e.getCode() == KeyCode.UP) {
                    p1.moveUp();
                    // send new paddle position
                    p.csc.sendPaddlePos((int)p1.getY());
                } else if (e.getCode() == KeyCode.DOWN) {
                    p1.moveDown(windowY);
                    // send new paddle position
                    p.csc.sendPaddlePos((int)p1.getY());
                }
            } else {
                if (e.getCode() == KeyCode.UP) {
                    p2.moveUp();
                    // send new paddle position
                    p.csc.sendPaddlePos((int)p2.getY());
                } else if (e.getCode() == KeyCode.DOWN) {
                    p2.moveDown(windowY);
                    // send new paddle position
                    p.csc.sendPaddlePos((int)p2.getY());
                }
            }
        });

        p1.setOnKeyReleased(e -> {
            if (p.playerID == 1) {
                p1.stopMovement();
            } else {
                p2.stopMovement();
            }
        });

        // Window Setup
        pane.setStyle("-fx-background-color: black;");
        pane.setPrefSize(windowX, windowY);
        primaryStage.setTitle("Player #" + p.playerID + " Networked Pong");
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();

        p1.requestFocus();

        // Quit application when window is closed
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

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
