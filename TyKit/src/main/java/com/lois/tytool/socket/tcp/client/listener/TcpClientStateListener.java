package com.lois.tytool.socket.tcp.client.listener;


import com.lois.tytool.socket.tcp.client.TcpClient;

/**
 * TCP客户端状态监听
 * @author Administrator
 */
public interface TcpClientStateListener {
    /**
     * 客户端连接服务器成功
     * @param client    客户端对象
     */
    void onConnected(TcpClient client);

    /**
     * 客户端和服务器断开连接
     * @param client    客户端对象
     * @param msg       描述信息
     * @param e         异常信息
     */
    void onDisconnected(TcpClient client, String msg, Exception e);
}
