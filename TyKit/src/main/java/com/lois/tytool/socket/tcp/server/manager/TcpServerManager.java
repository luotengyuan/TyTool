package com.lois.tytool.socket.tcp.server.manager;

import com.lois.tytool.socket.tcp.server.TcpServer;

import java.util.HashSet;
import java.util.Set;

/**
 * tcpserver
 * @author Administrator
 */
public class TcpServerManager {
    private static Set<TcpServer> sMXTcpServers = new HashSet<>();

    public static void putTcpServer(TcpServer server) {
        sMXTcpServers.add(server);
    }

    public static TcpServer getTcpServer(int port) {
        for (TcpServer ts : sMXTcpServers) {
            if (ts.getPort() == port) {
                return ts;
            }
        }
        return null;
    }
}
