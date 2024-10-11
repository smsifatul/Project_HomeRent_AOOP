import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlersList = new ArrayList<>();
    private Socket clientSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String clientUserName;
    private static final String MSG_LOG_FILE = "D:\\Spring 2024\\AOP Lab\\Home Assignment\\ChotBot\\src\\chat.txt";
    private Server server;

    public ClientHandler(Socket clientSocket, Server server) {
        try {
            this.clientSocket = clientSocket;
            this.server = server;
            this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.clientUserName = reader.readLine();

            clientHandlersList.add(this);
            broadCastMsg("SERVER: " + clientUserName + " has entered the Chat!!!");

            sendPrevMsgToNewClient();

        } catch (IOException e) {
            closeAll();
        }
    }

    public void broadCastMsg(String msgToSend) {
        for (ClientHandler clientHandler : clientHandlersList) {
            try {
                if (!clientHandler.clientUserName.equals(clientUserName)) {
                    clientHandler.writer.write(msgToSend);
                    clientHandler.writer.newLine();
                    clientHandler.writer.flush();
                }
            } catch (IOException e) {
                closeAll();
            }
        }
        server.receiveMsg(msgToSend);
    }

    public void removeClientHandler() {
        clientHandlersList.remove(this);
        broadCastMsg("SERVER: " + clientUserName + " has left the Chat!!!");
    }

    public void closeAll() {
        removeClientHandler();
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logMsgToFile(String msg) {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(MSG_LOG_FILE, true))) {
            printWriter.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPrevMsgToNewClient() {
        try (BufferedReader buffReader = new BufferedReader(new FileReader(MSG_LOG_FILE))) {
            String prevMsg;
            while ((prevMsg = buffReader.readLine()) != null) {
                writer.write(prevMsg);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String msgFromClient;
        while (clientSocket.isConnected()) {
            try {
                msgFromClient = reader.readLine();
                broadCastMsg(msgFromClient);
                logMsgToFile(msgFromClient);

            } catch (IOException e) {
                closeAll();
                break;
            }
        }
    }
}
