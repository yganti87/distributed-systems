package com.distributed.systems.practice;

import com.distributed.systems.practice.transport.udp.UdpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final String SERVER = "server";
    private static final String CLIENT = "client";
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static void main(String[] args) throws Exception
    {
        String mode = args[0];
        if (mode.equals(SERVER)) {
            final int port = Integer.parseInt(args[1]);
            final UdpServer server = new UdpServer(port);
            executorService.submit(server);
            while(!Thread.currentThread().isInterrupted()) {
                LOGGER.info("message=applicationMainThread, sleeping");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    LOGGER.info("message=applicationMainThread, interrupted");
                }
            }
            server.stop();
            LOGGER.info("message=applicationExit");
        } else if (mode.equals(CLIENT)) {
            final String targetHost = args[1];
            final InetAddress inetAddress = InetAddress.getByName(targetHost);
            final int targetPort = Integer.parseInt(args[2]);
            final AtomicInteger messageCounter = new AtomicInteger(0);
            try {
                final DatagramSocket datagramSocket = new DatagramSocket();
                while(!Thread.currentThread().isInterrupted()) {
                    String message = "message number : " + messageCounter.getAndIncrement();
                    final byte[] sendBytes = message.getBytes();
                    final DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, inetAddress, targetPort);
                    datagramSocket.send(sendPacket);
                    LOGGER.info("message=sentPacket, sleeping");
                    Thread.sleep(5000);
                }
            } catch (SocketException se) {
                LOGGER.error("messaage=errorCreatingDatagramSocket, exception={}", se);
            } catch (InterruptedException ie) {
                LOGGER.info("message=applicationMainThread, interrupted");
            }
        }
    }
}
