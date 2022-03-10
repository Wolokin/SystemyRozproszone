package homework.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

        try(ServerSocket serverSocket = new ServerSocket(Server.SERVER_PORT)) {
            socketReference = serverSocket;
            while(!serverSocket.isClosed()) {
                Socket newClientSocket = serverSocket.accept();
                System.out.println("New connection request received");
                ConnectionHandler newClientHandler = new ConnectionHandler(server, newClientSocket);
                threadPool.submit(newClientHandler);
            }
        } catch (IOException e) {
            System.out.println("shutting down server");
        }
        threadPool.shutdown();
    }

    public void interrupt() {
        if(!socketReference.isClosed()) {
            try {
                socketReference.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
