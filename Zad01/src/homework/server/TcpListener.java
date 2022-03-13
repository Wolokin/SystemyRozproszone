package homework.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpListener implements Runnable {
    private final Server server;
    private ServerSocket socketReference;

    private final ExecutorService threadPool;

    public TcpListener(Server server) {
        this.server = server;
        threadPool = Executors.newFixedThreadPool(Server.MAX_CLIENTS);
    }

    @Override
    public void run() {
        List<Socket> clientSockets = new ArrayList<>();

        try(ServerSocket serverSocket = new ServerSocket(Server.SERVER_PORT)) {
            socketReference = serverSocket;
            while(!serverSocket.isClosed()) {
                Socket newClientSocket = serverSocket.accept();
                clientSockets.add(newClientSocket);
                System.out.println("New connection request received");
                ConnectionHandler newClientHandler = new ConnectionHandler(server, newClientSocket);
                threadPool.submit(newClientHandler);
            }
        } catch (IOException e) {
            System.out.println("tcp socket closed");
        }
        threadPool.shutdown();
        for(Socket socket : clientSockets) {
            if(!socket.isClosed()){
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public void interrupt() {
        if(!socketReference.isClosed()) {
            try {
                socketReference.close();
            } catch (IOException ignored) {
            }
        }
    }
}
