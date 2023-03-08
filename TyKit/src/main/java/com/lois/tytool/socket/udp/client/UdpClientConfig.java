package com.lois.tytool.socket.udp.client;

import com.lois.tytool.base.constant.FileConstants;

/**
 * udp配置
 * @author Administrator
 */
public class UdpClientConfig {
    /**
     * 默认编码
     */
    private String charsetName = FileConstants.ENCODE_UTF_8;
    /**
     * 接受消息的超时时间,0为无限大
     */
    private long receiveTimeout = 10000;

    public UdpClientConfig() {
    }

    public String getCharsetName() {
        return charsetName;
    }

    public UdpClientConfig setCharsetName(String charsetName) {
        this.charsetName = charsetName;
        return this;
    }

    public long getReceiveTimeout() {
        return receiveTimeout;
    }

    public UdpClientConfig setReceiveTimeout(long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
        return this;
    }
}
