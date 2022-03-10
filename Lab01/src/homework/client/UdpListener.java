package homework.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpListener implements Runnable {
    private final DatagramSocket socket;

    public UdpListener(DatagramSocket socket) {
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
                System.out.print("\r"+msg+" ".repeat(100)+"\n> ");
            } catch (IOException e) {
                System.out.println("udp listener stopped");
            }
        }
    }
}
