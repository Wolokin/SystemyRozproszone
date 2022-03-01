package zad03;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class JavaUdpServer {

    public static int toInt(byte[] bytes, int length) {
        int val = 0;
        for(int i = 0; i < length; i++) {
            byte b = bytes[i];
            val += ((b & 0xFF) << (i*8));
        }
        return val;
    }

    public static void main(String args[])
    {
        System.out.println("JAVA UDP SERVER");

        int portNumber = 9008;
        try (DatagramSocket socket = new DatagramSocket(portNumber)) {
            byte[] receiveBuffer = new byte[1024];

            while (true) {
                Arrays.fill(receiveBuffer, (byte) 0);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
//                String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
                int msg = toInt(receivePacket.getData(), receivePacket.getLength());
                System.out.println("received msg: " + msg);

                byte[] sendBuffer = ByteBuffer.allocate(4).putInt(msg+1).array();


                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, receivePacket.getAddress(), receivePacket.getPort());
                socket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
