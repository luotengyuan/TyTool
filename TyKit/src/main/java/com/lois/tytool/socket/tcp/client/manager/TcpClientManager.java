package com.lois.tytool.socket.tcp.client.manager;

import com.lois.tytool.socket.tcp.client.TcpClient;
import com.lois.tytool.socket.bean.IpPort;

import java.util.HashSet;
import java.util.Set;

/**
 * TcpClient的管理者
 * @author Administrator
 */
public class TcpClientManager {
    private static Set<TcpClient> sMXTcpClients = new HashSet<>();

    public static void putTcpClient(TcpClient XTcpClient) {
        sMXTcpClients.add(XTcpClient);
    }

    public static TcpClient getTcpClient(IpPort ipPort) {
        for (TcpClient tc : sMXTcpClients) {
            if (tc.getTargetInfo().equals(ipPort)) {
                return tc;
            }
        }
        return null;
    }
}
