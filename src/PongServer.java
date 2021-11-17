import java.io.*;
import java.net.*;

public class PongServer {

    private ServerSocket ss;
    private int numPlayers;
    private ServerSideConnection player1;
    private ServerSideConnection player2;
    private int player1PaddlePos;
    private int player2PaddlePos;

    public PongServer() {
        System.out.println("----Game Server----");
        numPlayers = 0;
        try {
            ss = new ServerSocket(51734); // same as in player CSC Constructor
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
                ServerSideConnection ssc = new ServerSideConnection(s, numPlayers); // numPlayers functions as the playerID here
                if (numPlayers == 1) {
                    player1 = ssc;
                } else {
                    player2 = ssc;
                }
                Thread t = new Thread(ssc);
                t.start();
            }
            System.out.println("We now have 2 players. No longer accepting connections.");
            System.out.println("Game starts now.");

        } catch (IOException ex) {
            System.out.println("IOException from acceptConnections()");
        }
    }

    private class ServerSideConnection implements Runnable {

        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int playerID;

        public ServerSideConnection(Socket s, int id) {
            socket = s;
            playerID = id;
            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            } catch(IOException ex) {
                System.out.println("IOException from SSC constructor");
            }
        }

        public void run() {
            try {
                dataOut.writeInt(playerID); // first write in server
                dataOut.flush();

                while (true) {
                    if (playerID == 1) {
                        // read int coming from player 1
                        player1PaddlePos = dataIn.readInt();
                        player2.sendPaddlePos(player1PaddlePos);
                    } else {
                        // read int coming from player 2
                        player2PaddlePos = dataIn.readInt();
                        player1.sendPaddlePos(player2PaddlePos);
                    }
                }
            } catch (IOException ex) {
                System.out.println("IOException from SSC run()");
            }
        }

        public void sendPaddlePos(int n) {
            try {
                dataOut.writeInt(n);
                dataOut.flush();
            } catch (IOException ex) {
                System.out.println("IOException from sendPaddlePos() SSC");
            }
        }
    }

    public static void main(String[] args) {
        PongServer ps = new PongServer();
        ps.acceptConnections();
    }
}
