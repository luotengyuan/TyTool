package com.lois.tytool.socket.tcp.server;

import com.lois.tytool.socket.tcp.client.TcpConnConfig;

/**
 * server配置
 * @author Administrator
 */
public class TcpServerConfig extends TcpConnConfig{
    private int maxClientSize = Integer.MAX_VALUE;

    public TcpServerConfig() {
        super();
    }

    public int getMaxClientSize() {
        return maxClientSize;
    }

    public void setMaxClientSize(int maxClientSize) {
        this.maxClientSize = maxClientSize;
    }
}
