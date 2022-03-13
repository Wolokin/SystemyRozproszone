package homework.server;

import homework.common.ControlMessage;
import homework.common.Specs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static final String SERVER_ADDRESS = Specs.SERVER_ADDRESS;
    public static final int SERVER_PORT = Specs.SERVER_PORT;
    public static int MAX_CLIENTS = 10;

    private final HashMap<String, ConnectionHandler> clients;

    public Server() {
        this.clients = new LinkedHashMap<>();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        ExecutorService listeners = Executors.newFixedThreadPool(2);
        TcpListener tcpListener = new TcpListener(this);
        listeners.submit(tcpListener);
        UdpListener udpListener = new UdpListener(this);
        listeners.submit(udpListener);

        System.out.println("Server ready");

        awaitCommands();

        listeners.shutdown();
        tcpListener.interrupt();
        udpListener.interrupt();
    }

    public boolean isNameUsed(String name) {
        return clients.containsKey(name);
    }

    public void registerUser(ConnectionHandler connectionHandler) {
        broadcastControlMessage(connectionHandler.getName() + " joined chat.");
        clients.put(connectionHandler.getName(), connectionHandler);
        System.out.println("New user connected: " + connectionHandler.getName());
    }

    public void unregisterUser(ConnectionHandler connectionHandler) {
        if (clients.remove(connectionHandler.getName()) != null) {
            broadcastControlMessage(connectionHandler.getName() + " left chat.");
            System.out.println("User disconnected: " + connectionHandler.getName());
        }
    }

    private void broadcastControlMessage(String msg) {
        for (ConnectionHandler client : clients.values()) {
            client.getOutput().println(msg);
        }
    }

    public void broadcastMessage(String msg, ConnectionHandler sender) {
        for (ConnectionHandler client : clients.values()) {
            if (client != sender) {
                client.getOutput().println("[" + sender.getName() + "]: " + msg);
            }
        }
    }

    public ConnectionHandler getClient(String name) {
        return clients.get(name);
    }

    public Collection<ConnectionHandler> getClients() {
        return clients.values();
    }

    private void awaitCommands() {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String command = "";
        do {
            try {
                command = stdin.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!command.equalsIgnoreCase(ControlMessage.QUIT));
        broadcastControlMessage("server shutting down");
    }
}
