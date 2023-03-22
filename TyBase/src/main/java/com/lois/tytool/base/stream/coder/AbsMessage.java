package com.lois.tytool.base.stream.coder;

/**
 * @Description 抽象消息
 * @Author Lois
 * @Date 2023/2/27 23:09
 */
public abstract class AbsMessage<T> implements IMessage<T> {

    protected byte[] mDataBytes;
    protected T mDataObj;

    public AbsMessage() {

    }

    public AbsMessage(T obj) {
        this.mDataObj = obj;
    }

    public AbsMessage(byte[] bytes) {
        this.mDataBytes = bytes;
    }

    public byte[] getDataBytes() {
        return mDataBytes;
    }

    public void setDataBytes(byte[] mDataBytes) {
        this.mDataBytes = mDataBytes;
    }

    public T getDataObj() {
        return mDataObj;
    }

    public void setDataObj(T mDataObj) {
        this.mDataObj = mDataObj;
    }
}
