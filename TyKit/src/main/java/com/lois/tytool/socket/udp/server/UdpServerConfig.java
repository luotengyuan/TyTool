package com.lois.tytool.socket.udp.server;

import com.lois.tytool.socket.udp.client.UdpClientConfig;

/**
 * udp配置
 * @author Administrator
 */
public class UdpServerConfig extends UdpClientConfig {
    /**
     * 本地端口
     */
    private int localPort = -1;

    public UdpServerConfig() {
        super();
    }

    public int getLocalPort() {
        return localPort;
    }

    public UdpServerConfig setLocalPort(int localPort) {
        this.localPort = localPort;
        return this;
    }
}
