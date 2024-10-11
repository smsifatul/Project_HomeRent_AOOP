import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

public class Server {
    private final ServerSocket serverSock;
    private final Hashtable<Socket, ClientHandler> clientSocketMap;

    public Server(ServerSocket serverSock) {
        this.serverSock = serverSock;
        this.clientSocketMap = new Hashtable<>();
    }

    public void startServer() {
        try {
            while (!serverSock.isClosed()) {
                Socket clientSock =  serverSock.accept();
                System.out.println("A new Client has Connected!!!");
                ClientHandler clientHandler = new ClientHandler(clientSock, this);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServerSock() {
        try {
            if (serverSock != null) {
                serverSock.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSock = new ServerSocket(8080);
        Server server = new Server(serverSock);
        server.startServer();
    }

    public void addClientSocket(Socket clientSock, ClientHandler clientHandler) {
        clientSocketMap.put(clientSock, clientHandler);
    }

    public void removeClientSocket(Socket clientSock) {
        clientSocketMap.remove(clientSock);
    }

    public void receiveMsg(String msg) {
        System.out.println(msg);
    }
}
