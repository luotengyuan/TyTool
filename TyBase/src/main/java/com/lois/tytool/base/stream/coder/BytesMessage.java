package com.lois.tytool.base.stream.coder;

/**
 * @Description 字节数组消息
 * @Author Lois
 * @Date 2023/2/27 22:59
 */
public class BytesMessage extends AbsMessage<byte[]> {

    public BytesMessage() {
        super();
    }

    public BytesMessage(byte[] obj) {
        mDataBytes = obj;
        mDataObj = obj;
    }

    @Override
    public byte[] encode() {
        return encode(mDataBytes);
    }

    @Override
    public byte[] encode(byte[] bean) {
        return bean;
    }

    @Override
    public byte[] decode() {
        return decode(mDataObj);
    }

    @Override
    public byte[] decode(byte[] data) {
        return data;
    }
}
