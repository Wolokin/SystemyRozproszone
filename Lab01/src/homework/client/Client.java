package homework.client;

import homework.common.ControlMessage;
import homework.common.Specs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Client {
    public static final String hostName = Specs.SERVER_ADDRESS;
    public static final int portNumber = Specs.SERVER_PORT;

    public static final String multicastAddress = Specs.MULTICAST_ADDRESS;
    public static final int multicastPort = Specs.MULTICAST_PORT;

    private PrintWriter output;
    private BufferedReader input;

    private boolean isRegistered = false;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition isRegisteredCond = lock.newCondition();

    private String name;

    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        client.start();
    }

    private void waitForRegistration() throws InterruptedException {
        lock.lock();
        try {
            while(!isRegistered) {
                isRegisteredCond.await();
            }
        }
        finally {
            lock.unlock();
        }
    }

    public void clientRegistered(String name) {
        isRegistered = true;
        this.name = name;
        lock.lock();
        try {
            isRegisteredCond.signal();
        } finally {
            lock.unlock();
        }
    }

    public String getName() {
        return name;
    }

    public void start() {
        try (Socket socket = new Socket(hostName, portNumber)) {
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            ExecutorService threads = Executors.newFixedThreadPool(3);
            TcpListener tcpListener = new TcpListener(this, socket);
            threads.submit(tcpListener);

            waitForRegistration();

            DatagramSocket datagramSocket = new DatagramSocket(socket.getLocalPort());
            InetAddress udpAddress = InetAddress.getByName(hostName);

            MulticastSocket multicastSocket = new MulticastSocket(multicastPort);
            InetAddress multicastAddr = InetAddress.getByName(multicastAddress);
            InetSocketAddress multicastGroup = new InetSocketAddress(multicastAddr, multicastPort);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(multicastAddr);
            multicastSocket.joinGroup(multicastGroup, networkInterface);

            UdpListener udpListener = new UdpListener(datagramSocket);
            threads.submit(udpListener);

            MulticastListener multicastListener = new MulticastListener(multicastSocket, this);
            threads.submit(multicastListener);

            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            while(!socket.isClosed()) {
                String msg = stdin.readLine();
                switch (msg.toUpperCase()) {
                    case ControlMessage.QUIT -> {
                        output.println(ControlMessage.QUIT);
                        threads.shutdown();
                        if(!datagramSocket.isClosed()) {
                            datagramSocket.close();
                        }
                        if(!multicastSocket.isClosed()) {
                            multicastSocket.close();
                        }
                        return;
                    }
                    case ControlMessage.MEDIA_UNICAST, ControlMessage.MEDIA_MULTICAST -> {
                        String reply = name + "\n";
                        reply += Files.readString(Path.of("src/homework/art.txt"));
                        byte[] bytes = reply.getBytes(StandardCharsets.UTF_8);
                        DatagramPacket datagramPacket;
                        if (msg.equalsIgnoreCase(ControlMessage.MEDIA_UNICAST)) {
                            datagramPacket = new DatagramPacket(bytes, bytes.length, udpAddress, portNumber);
                            datagramSocket.send(datagramPacket);
                        } else {
                            datagramPacket = new DatagramPacket(bytes, bytes.length, multicastAddr, multicastPort);
                            multicastSocket.send(datagramPacket);
                        }
                    }
                    default -> output.println(msg);
                }
                System.out.print("> ");
            }
        } catch (Exception e) {
            System.out.println("exiting");
        }
    }
}
