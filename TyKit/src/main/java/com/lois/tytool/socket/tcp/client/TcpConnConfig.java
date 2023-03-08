package com.lois.tytool.socket.tcp.client;

import com.lois.tytool.base.constant.FileConstants;
import com.lois.tytool.base.stream.sticky.NoStickPackage;
import com.lois.tytool.base.stream.sticky.IStickPackage;

import java.nio.ByteOrder;

/**
 * 连接配置
 * @author Administrator
 */
public class TcpConnConfig {
    /**
     * 默认编码
     */
    private String charsetName = FileConstants.ENCODE_UTF_8;
    /**
     * 连接超时时间
     */
    private long connTimeout = 5000;
    /**
     * 接受消息的超时时间,0为无限大
     */
    private long receiveTimeout = 0;
    /**
     * 大端还是小端
     */
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    /**
     * 是否重连
     */
    private boolean isReconnect = false;
    /**
     * 粘包处理类
     */
    private IStickPackage stickPackage = new NoStickPackage();
    /**
     * 本地端口
     */
    private int localPort = -1;

    public TcpConnConfig() {
    }

    public String getCharsetName() {
        return charsetName;
    }

    public TcpConnConfig setCharsetName(String charsetName) {
        this.charsetName = charsetName;
        return this;
    }

    public long getConnTimeout() {
        return connTimeout;
    }

    public TcpConnConfig setConnTimeout(long connTimeout) {
        this.connTimeout = connTimeout;
        return this;
    }

    public long getReceiveTimeout() {
        return receiveTimeout;
    }

    public TcpConnConfig setReceiveTimeout(long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
        return this;
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public TcpConnConfig setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
        return this;
    }

    public boolean isReconnect() {
        return isReconnect;
    }

    public TcpConnConfig setReconnect(boolean reconnect) {
        isReconnect = reconnect;
        return this;
    }

    public IStickPackage getStickPackage() {
        return stickPackage;
    }

    public TcpConnConfig setStickPackage(IStickPackage stickPackage) {
        this.stickPackage = stickPackage;
        return this;
    }

    public int getLocalPort() {
        return localPort;
    }

    public TcpConnConfig setLocalPort(int localPort) {
        this.localPort = localPort;
        return this;
    }
}
