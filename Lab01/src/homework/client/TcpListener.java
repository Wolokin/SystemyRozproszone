package homework.client;

import homework.common.ControlMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpListener implements Runnable {
    private final Client client;
    private final Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public TcpListener(Client client, Socket socket) {
        this.client = client;
        this.socket = socket;
    }

    private void register() throws IOException {
        String reply;
        while(!(reply = input.readLine()).startsWith(ControlMessage.REGISTERED)) {
            System.out.print(reply);
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            output.println(stdin.readLine());
        }
        client.clientRegistered(reply.split(":",2)[1]);
    }

    @Override
    public void run() {
        try {
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            register();

            String msg;
            while(!socket.isClosed() && (msg = input.readLine()) != null) {
                System.out.print("\r"+msg+" ".repeat(100)+"\n> ");
            }
        } catch (IOException e) {
            System.out.println("tcp listener stopped");
        }
    }
}
