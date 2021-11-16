import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;

public class Player extends JFrame {
    private int width = 1200;
    private int height = 800;
    private Container contentPane;
    private JTextArea message;
    private int playerID;
    private int otherPlayer;
    private int myPoints;
    private int enemyPoints;

    private ClientSideConnection csc;

    public Player() {
        contentPane = this.getContentPane();
        message = new JTextArea();
        myPoints = 0;
        enemyPoints = 0;
    }

    public void setUpGUI() {
        this.setSize(width, height);
        this.setTitle("Player #" + playerID);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contentPane.setLayout(new GridLayout(1, 5));
        contentPane.add(message);
        message.setText("Networked Multiplayer Pong");
        message.setWrapStyleWord(true);
        message.setLineWrap(true);
        message.setEditable(false);

        if (playerID == 1) {
            message.setText("You are player #1");
            otherPlayer = 2;
        }
        else {
            message.setText("You are player #2");
            otherPlayer = 1;
            Thread t = new Thread(new Runnable() {
                public void run() {
                    updateScore();
                }
            });
            t.start();
        }

        this.setVisible(true);
    }

    public void connectToServer() {
        csc = new ClientSideConnection();
    }


    public void updateScore() {
        int n = csc.receiveButtonNum();
        message.setText("Your enemy clicked button #" + n + ". Your turn.");
        //enemyPoints += values[n-1];
        System.out.println("Your enemy has " + enemyPoints + " points");
//        if (playerID == 1 && turnsMade == maxTurns) {
//            checkWinner();
//        }
    }

    private void checkWinner() {
        if (myPoints > enemyPoints) {
            message.setText("You WON!\n" + "YOU: " + myPoints + "\n" + "ENEMY: " + enemyPoints + "\n");
        }
        else if (myPoints < enemyPoints) {
            message.setText("You LOST!\n" + "YOU: " + myPoints + "\n" + "ENEMY: " + enemyPoints + "\n");
        }
        else {
            message.setText("It's a TIE!\n" + "YOU: " + myPoints + "\n" + "ENEMY: " + enemyPoints + "\n");
        }

        csc.closeConnection();
    }

    // Client Connection Inner Class
    private class ClientSideConnection {

        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public ClientSideConnection () {
            System.out.println("----Client----");
            try {
                socket = new Socket("localhost", 51734); // this port needs to be the same as specified in the ServerSocket in GameServer.java
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                playerID = dataIn.readInt();
                System.out.println("Connected to server as Player #" + playerID + ".");
            }
            catch (IOException ex) {
                System.out.println("IO Exception from CSC constructor");
            }
        }

        public void sendButtonNum (int n) {
            try {
                dataOut.writeInt(n);
                dataOut.flush();
            }
            catch (IOException ex) {
                System.out.println("IOException from sendButtonNum() CSC");
            }
        }

        public int receiveButtonNum() {
            int n = -1;
            try {
                n = dataIn.readInt();
                System.out.println("Player #" + otherPlayer + " clicked button #" + n);
            }
            catch (IOException ex) {
                System.out.println("IOException from receiveButtonNum() CSC");
            }
            return n;
        }

        public void closeConnection() {
            try {
                socket.close();
                System.out.println("----CONNECTION CLOSED----");
            }
            catch (IOException ex) {
                System.out.println("IOException on closeConnection() CSC");
            }
        }
    }

    public static void main(String[] args) {
        Player p = new Player();
        p.connectToServer();
        p.setUpGUI();
    }

}