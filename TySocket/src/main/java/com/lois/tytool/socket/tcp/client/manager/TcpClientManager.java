package com.lois.tytool.socket.tcp.client.manager;

import com.lois.tytool.socket.tcp.client.TcpClient;
import com.lois.tytool.socket.tcp.client.bean.TargetInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * tcpclient的管理者
 */
public class TcpClientManager {
    private static Set<TcpClient> sMXTcpClients = new HashSet<>();

    public static void putTcpClient(TcpClient XTcpClient) {
        sMXTcpClients.add(XTcpClient);
    }

    public static TcpClient getTcpClient(TargetInfo targetInfo) {
        for (TcpClient tc : sMXTcpClients) {
            if (tc.getTargetInfo().equals(targetInfo)) {
                return tc;
            }
        }
        return null;
    }
}
