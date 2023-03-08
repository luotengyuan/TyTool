package com.lois.tytool.base.stream.coder;

import com.lois.tytool.base.constant.FileConstants;

import java.io.UnsupportedEncodingException;

/**
 * @Description 字符串消息
 * @Author Lois
 * @Date 2023/2/27 22:59
 */
public class StringMessage extends AbsMessage<String> {

    /**
     * 默认编码
     */
    private String charsetName = FileConstants.ENCODE_UTF_8;

    public StringMessage() {
        super();
    }

    public StringMessage(String obj) {
        super(obj);
    }

    public StringMessage(byte[] bytes) {
        super(bytes);
    }

    public StringMessage(String obj, String charsetName) {
        super(obj);
        this.charsetName = charsetName;
    }

    public StringMessage(byte[] bytes, String charsetName) {
        super(bytes);
        this.charsetName = charsetName;
    }

    @Override
    public byte[] encode() {
        return encode(mDataObj);
    }

    @Override
    public byte[] encode(String bean) {
        if (bean == null) {
            return null;
        }
        if (charsetName == null) {
            return bean.getBytes();
        }
        try {
            return bean.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String decode() {
        return decode(mDataBytes);
    }

    @Override
    public String decode(byte[] data) {
        if (data == null) {
            return null;
        }
        if (charsetName == null) {
            return new String(data);
        }
        try {
            return new String(data, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }
}
