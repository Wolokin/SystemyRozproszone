package homework.server;

import homework.common.ControlMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;

public class ConnectionHandler implements Runnable {
    private final Server server;
    private final Socket clientSocket;

    private PrintWriter output;
    private BufferedReader input;

    private String name;

    public ConnectionHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    public Socket getSocket() {
        return clientSocket;
    }

    public String getName() {
        return name;
    }

    public PrintWriter getOutput() {
        return output;
    }

    private void generateName() {
        Random random = new Random();
        do {
            this.name = "Client" + random.nextInt(1000, 9999);
        } while(server.isNameUsed(this.name));
    }

    private void askForName() throws IOException {
        generateName();

        output.println("Please enter your name (default: " + name + "): ");
        String reply;
        while ((reply = input.readLine()).length() != 0 && server.isNameUsed(reply)) {
            output.println("Name " + reply + " is taken, please choose another one (default: " + name + "): ");
        }

        if (reply.length() > 0) this.name = reply;
        server.registerUser(this);
        output.println(ControlMessage.REGISTERED+":"+name);
        output.println("Successfully registered as " + name);
    }

    private void processIncomingMessages() throws IOException {
        String msg;
        while (!clientSocket.isClosed() && (msg = input.readLine()) != null) {
            if (ControlMessage.QUIT.equalsIgnoreCase(msg)) {
                server.unregisterUser(this);
                return;
            } else {
                server.broadcastMessage(msg, this);
            }
        }
    }

    @Override
    public void run() {
        try {
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            askForName();

            processIncomingMessages();

        } catch(IOException e) {
            System.out.println(name+" process has ended");
        } finally {
            server.unregisterUser(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionHandler client = (ConnectionHandler) o;
        return getName().equals(client.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
