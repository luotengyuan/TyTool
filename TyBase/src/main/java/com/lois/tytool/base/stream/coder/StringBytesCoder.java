package com.lois.tytool.base.stream.coder;

/**
 * @Description TODO
 * @Author Luo.T.Y
 * @Date 2023/2/15
 * @Time 19:59
 */
public class StringBytesCoder implements IBytesCoder<String> {



    @Override
    public String decode(byte[] data) {
        return null;
    }

    @Override
    public byte[] encode(String bean) {
        return new byte[0];
    }
}
