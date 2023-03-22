package com.lois.tytool.socket.udp.server.manager;

import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 */
public class UdpServerManager {
    private static Map<Integer, DatagramSocket> sDatagramSockets = new HashMap();

    public static void putUdpSocket(DatagramSocket socket) {
        sDatagramSockets.put(socket.getLocalPort(), socket);
    }

    public static DatagramSocket getUdpSocket(int port) {
        return sDatagramSockets.get(port);
    }
}
