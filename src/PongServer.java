import java.io.*;
import java.net.*;

public class PongServer {

    private ServerSocket ss;
    private int numPlayers;

    public PongServer() {
        System.out.println("----Game Server----");
        numPlayers = 0;
        try {
            ss = new ServerSocket(51734);
        } catch (IOException ex) {
            System.out.println("IOException from PongServer Constructor");
        }
    }

    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections...");
            while (numPlayers < 2) {
                Socket s = ss.accept();
                numPlayers++;
                System.out.println("Player #" + numPlayers + " has connected.");
            }
            System.out.println("We now have 2 players. No longer accepting connections.");
        } catch (IOException ex) {
            System.out.println("IOException from acceptConnections()");
        }
    }

    public static void main(String[] args) {
        PongServer ps = new PongServer();
        ps.acceptConnections();
    }
}
