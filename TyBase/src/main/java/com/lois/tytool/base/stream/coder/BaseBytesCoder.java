package com.lois.tytool.base.stream.coder;

/**
 * @Description TODO
 * @Author Luo.T.Y
 * @Date 2023/2/15
 * @Time 19:58
 */
public class BaseBytesCoder implements IBytesCoder<byte[]> {
    @Override
    public byte[] decode(byte[] data) {
        return data;
    }

    @Override
    public byte[] encode(byte[] bean) {
        return bean;
    }
}
