package com.lois.tytool.socket.tcp.client.helper.decode;

import com.lois.tytool.socket.tcp.client.TcpConnConfig;
import com.lois.tytool.socket.tcp.client.bean.TargetInfo;

/**
 * 解析消息的处理
 */
public interface AbsDecodeHelper {
    /**
     *
     * @param data  完整的数据包
     * @param targetInfo    对方的信息(ip/port)
     * @param tcpConnConfig    tcp连接配置，可自定义
     * @return
     */
    byte[][] execute(byte[] data, TargetInfo targetInfo, TcpConnConfig tcpConnConfig);
}
