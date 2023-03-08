package com.lois.tytool.socket.udp.client.listener;

import com.lois.tytool.base.stream.coder.IMessage;
import com.lois.tytool.socket.udp.client.UdpClient;

/**
 * UDP客户端状态监听
 * @author Administrator
 */
public interface UdpClientStateListener {
    /**
     * 消费发送成功
     * @param client
     * @param msg
     */
    void onSended(UdpClient client, IMessage msg);

    /**
     * 消息发送错误
     * @param client
     * @param msg
     * @param e
     */
    void onError(UdpClient client, String msg, Exception e);

}
