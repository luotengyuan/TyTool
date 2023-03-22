package com.lois.tytool.socket.tcp.server.listener;

import com.lois.tytool.socket.tcp.client.TcpClient;
import com.lois.tytool.socket.tcp.server.TcpServer;

/**
 * tcpserver状态监听
 * @author Administrator
 */
public interface TcpServerStateListener {
    /**
     * 服务器创建成功
     * @param server
     */
    void onCreated(TcpServer server);

    /**
     * 服务器开启监听成功
     * @param server
     */
    void onListened(TcpServer server);

    /**
     * 收到客户端连接
     * @param server
     * @param tcpClient
     */
    void onAccept(TcpServer server, TcpClient tcpClient);

    /**
     * 客户端断开连接
     * @param server
     * @param tcpClient
     * @param msg
     * @param e
     */
    void onClientClosed(TcpServer server, TcpClient tcpClient, String msg, Exception e);

    /**
     * 服务器关闭
     * @param server
     * @param msg
     * @param e
     */
    void onServerClosed(TcpServer server, String msg, Exception e);
}
