package homework.server;

import homework.common.Specs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UdpListener implements Runnable {
    private final Server server;
    private DatagramSocket socketReference;

    public UdpListener(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(Server.SERVER_PORT)) {
            socketReference = socket;
            while (!socket.isClosed()) {
                byte[] buffer = new byte[8192];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(datagramPacket);


                String msg = new String(buffer, 0, datagramPacket.getLength());
                String[] split = msg.split("\n", 2);
                String name = split[0];
                msg = "[" + name + "]: \n" + split[1];

                System.out.println("Broadcasting multimedia data from "+name);

                InetAddress address = InetAddress.getByName(Server.SERVER_ADDRESS);
                byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);

                ConnectionHandler sender = server.getClient(name);
                for (ConnectionHandler client : server.getClients()) {
                    if (!client.equals(sender)) {
                        int port = client.getSocket().getPort();
                        socket.send(new DatagramPacket(bytes, bytes.length, address, port));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("udp socket closed");
        }
    }

    public void interrupt() {
        if(!socketReference.isClosed()) {
            socketReference.close();
        }
    }
}
