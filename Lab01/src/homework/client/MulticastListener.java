package homework.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class MulticastListener implements Runnable{
    private final Client client;
    private final MulticastSocket socket;

    public MulticastListener(MulticastSocket socket, Client client) {
        this.client = client;
        this.socket = socket;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                byte[] buffer = new byte[8192];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(datagramPacket);
                String msg = new String(buffer, 0, datagramPacket.getLength());
                String[] split = msg.split("\n",2);
                if(!client.getName().equals(split[0])) {
                    msg = "[" + split[0] + "]: \n" + split[1];
                    System.out.print("\r" + msg + " ".repeat(100) + "\n> ");
                }
            } catch (IOException e) {
                System.out.println("multicast listener stopped");
            }
        }
    }
}
