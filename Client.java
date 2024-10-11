import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket sock;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String userName;

    public Client(Socket sock, String userName) {
        try {
            this.sock = sock;
            this.writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            this.userName = userName;

            sendMessage(userName);

            listenForMsg();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String msg) {
        try {
            writer.write(msg);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForMsg() {
        new Thread(() -> {
            String msgFromServer;
            while (sock.isConnected()) {
                try {
                    msgFromServer = reader.readLine();
                    System.out.println(msgFromServer);
                } catch (IOException e) {
                    closeAll();
                }
            }
        }).start();
    }

    public void closeAll() {
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (sock != null) {
                sock.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Your Username: ");
        String userName = scanner.nextLine();

        Socket sock = new Socket("localhost", 8080);
        Client client = new Client(sock, userName);

        while (true) {
            String msgToSend = scanner.nextLine();
            client.sendMessage(userName + ": " + msgToSend);
        }
    }
}
