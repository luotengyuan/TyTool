package com.lois.tytool.socket.tcp.client.helper.decode;

import com.lois.tytool.socket.tcp.client.TcpConnConfig;
import com.lois.tytool.socket.tcp.client.bean.TargetInfo;

public class BaseDecodeHelper implements AbsDecodeHelper {
    @Override
    public byte[][] execute(byte[] data, TargetInfo targetInfo, TcpConnConfig tcpConnConfig) {
        return new byte[][]{data};
    }
}
