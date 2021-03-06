package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;
import java.net.*;


public class Player extends Application {
    private boolean playing = true;
    public int windowX = 1200;
    public int windowY = 800;
    private int playerID;
    private int otherPlayer; // not sure if this variable might be useful or not.
                            // I'm not using it for anything rly so if you don't use it for anu GUI stuff feel free to remove
    private boolean receiving = true;

    private ClientSideConnection csc;

    public void startReceivingPaddlePos(Paddle otherPaddle) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                while (receiving) {
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
            // Prompt the user to input the IP address of the server
            String host = JOptionPane.showInputDialog("Input server IP address:");
            try {
                // connect to the server address that the user inputted
                // has to be the exact same port as in application.PongServer Constructor
                socket = new Socket(host, 51734);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                playerID = dataIn.readInt(); // first read in client
                System.out.println("Connected to server as application.Player #" + playerID + ".");
            } catch (IOException ex) {
                System.out.println("IOException from CSC constructor");
            }
        }

        // method to send this player's paddle position to the server
        public void sendPaddlePos(int n) {
            try {
                if (receiving) {
                    dataOut.writeInt(n);
                } else { // if game is over send signal to server
                    dataOut.writeInt(-1);
                }
                dataOut.flush();
            } catch (IOException ex) {
                System.out.println("IOException from sendPaddlePos() CSC");
            }
        }

        // method to receive the other player's paddle position from the server
        public int receivePaddlePos(Paddle otherPaddle) {
            int n = -1;
            try {
                n = dataIn.readInt();
                otherPaddle.setY(n);
            } catch (IOException ex) {
                System.out.println("IOException from receivePaddlePos() CSC");
            }
            return n;
        }

        public void closeConnection() {
            try {
                socket.close();
                System.out.println("----CONNECTION CLOSED----");
            } catch (IOException ex) {
                System.out.println("IOException from closeConnection() CSC");
            }
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


        // Scoreboard
        Text p1Score = new Text("Player One: " + p1.score);;
        p1Score.setFill(Color.WHITE);
        p1Score.setFont(Font.font(30));
        p1Score.setFontSmoothingType(FontSmoothingType.LCD);
        p1Score.setFont(Font.font("Verdana", FontWeight.BOLD, 30));

        Text p2Score = new Text("Player Two: " + p2.score);;
        p2Score.setFill(Color.WHITE);
        p2Score.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        p2Score.setFontSmoothingType(FontSmoothingType.LCD);

        HBox hBox = new HBox(100);
        hBox.setPadding(new Insets(15, 12, 15, windowX/4));
        hBox.getChildren().addAll(p1Score, p2Score);
        hBox.setAlignment(Pos.CENTER);

        Pane playingField = new Pane(p1, ball, p2);

        Player p = new Player();
        p.connectToServer();

        StackPane rootPane = new StackPane();
        Scene scene = new Scene(rootPane);
        Pane scoreBoard = new Pane(hBox);
        rootPane.getChildren().addAll(playingField, scoreBoard);


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

        // application.Paddle movement
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
            if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.UP) {
                if (p.playerID == 1) {
                    p1.stopMovement();
                } else {
                    p2.stopMovement();
                }
            }
        });

        // Window Setup
        playingField.setStyle("-fx-background-color: black;");
        playingField.setPrefSize(windowX, windowY);
        primaryStage.setTitle("Player #" + p.playerID + " Networked Multiplayer Pong");
        primaryStage.setScene(scene);
        primaryStage.show();

        p1.requestFocus();

        // Quit application when window is closed
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });


        // application.Ball Movement
        if (playing) {
            Timeline animation = new Timeline(new KeyFrame(Duration.millis(10),
                    e -> {
                        try {
                            moveBall(ball, p1, p2, p, p1Score, p2Score);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }));
            animation.setCycleCount(Timeline.INDEFINITE);
            animation.play();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void moveBall(Ball ball, Paddle p1, Paddle p2, Player p, Text p1Score, Text p2Score) throws Exception {
        // only start ball movement once p2 has moved
        // if p2 has moved, that means both players are connected to the server
        // stop moving the ball once a player has more than 5 points (they win)
        if (playing && p2.getY() != -1 && p1.score < 5 && p2.score < 5) {
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
                score(p2, p, p1Score, p2Score);
                playing = false;
            }
            else if (ball.getX() > windowX) {
                score(p1, p, p1Score, p2Score);
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

    public void score(Paddle paddle, Player p, Text p1Score, Text p2Score) {
        paddle.score += 1;
        System.out.println("Player #" + paddle.playerID + " score: " + paddle.score);
        if (paddle.playerID == 1) {
            p1Score.setText("Player One: " + paddle.score);
        } else {
            p2Score.setText("Player Two: " + paddle.score);
        }
        // If a player has won
        if (paddle.score >= 5) {
            // stop the game
            p1Score.setText("Player #" + paddle.playerID );
            p2Score.setText(" has won the game!");
            System.out.println("Player #" + paddle.playerID + " has won the game!");
            p.receiving = false;
            p.csc.closeConnection();
        }
    }
}
