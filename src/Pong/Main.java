package src.Pong;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import src.Pong.Ball;


public class Main extends Application {

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
        pane.setStyle("-fx-background-color: black;");
        pane.setPrefSize(600, 400);
        primaryStage.setTitle("Peer to Peer Pong");
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();

        ball.startGame();

        if (ball.getBoundsInParent().intersects(p1.getBoundsInParent())) {
            ball.hit();
        }

        p1.requestFocus();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
