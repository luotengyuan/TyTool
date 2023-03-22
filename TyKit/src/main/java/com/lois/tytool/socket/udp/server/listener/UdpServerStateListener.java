package com.lois.tytool.socket.udp.server.listener;

import com.lois.tytool.socket.udp.server.UdpServer;

/**
 * @author Administrator
 */
public interface UdpServerStateListener {
    /**
     * UDP服务开启成功
     * @param server
     */
    void onStarted(UdpServer server);

    /**
     * UDP服务停止成功
     * @param server
     */
    void onStoped(UdpServer server);

    /**
     * UDP服务发生错误
     * @param server
     * @param msg
     * @param e
     */
    void onError(UdpServer server, String msg, Exception e);

}
