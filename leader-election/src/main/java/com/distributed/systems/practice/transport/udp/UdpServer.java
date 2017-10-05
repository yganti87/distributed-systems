package com.distributed.systems.practice.transport.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by yganti on 10/4/17.
 */
public class UdpServer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpServer.class);

    private final int port;
    private DatagramSocket datagramSocket;
    private AtomicBoolean interrupted = new AtomicBoolean(false);

    public UdpServer(int port) {
        this.port = port;
        try {
            datagramSocket = new DatagramSocket(port);
        } catch (Exception e) {
            LOGGER.error("message=errorCreatingDatagramSocket, serverId={}, exception={}", getServerId(), e);
            throw new RuntimeException(e);
        }
        LOGGER.info("message=UdpServerCreated");
    }

    public void stop() {
        interrupted.set(true);
        try {
            datagramSocket.close();
        } catch (Exception e) {
            LOGGER.error("message=errorClosingDatagramSocket, serverId={}, exception={}", getServerId(), e);
        }
    }

    public String getServerId() {
        return "localhost:" +  port;
    }

    @Override
    public void run() {
        while(!interrupted.get()) {
            try {
                final byte[] buf = new byte[256];
                final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                datagramSocket.receive(packet);
                final String message = new String(packet.getData(), 0, packet.getLength());
                final String sourceHostname = packet.getAddress().getHostName();
                final int sourcePort = packet.getPort();
                LOGGER.info("message=receivedData, serverId={}, sourceHostname={}, sourcePort={}, message={}", getServerId(),
                        sourceHostname, sourcePort, message);
            } catch (Exception e) {
                LOGGER.error("message=exceptionReadingData, serverId={}, exception={}", getServerId(), e);
            }
        }
    }
}
