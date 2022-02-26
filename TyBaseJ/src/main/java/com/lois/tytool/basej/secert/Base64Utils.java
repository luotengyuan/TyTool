package com.lois.tytool.basej.secert;

import com.lois.tytool.basej.string.StringUtils;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.net.util.Base64;

/**
 * Base64编解码工具
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public final class Base64Utils {
    private Base64Utils() {
    }


    /**
     * UTF-8编码格式的Base64编码
     * @param plainText 编码字节数组
     * @return base64字符串
     */
    public static String encode(String plainText) {
        return encode(plainText, CharEncoding.UTF_8);
    }

    /**
     * 字节数组的Base64编码
     * @param binary 编码字节数组
     * @return base64字符串
     */
    public static String encode(byte[] binary){
        return Base64.encodeBase64String(binary);
    }

    /**
     * 指定编码格式的Base64编码
     * @param plainText 编码文本
     * @param charsetName 编码格式
     * @return base64字符串
     */
    public static String encode(String plainText, String charsetName) {
        byte[] binary = StringUtils.stringToBytes(plainText, charsetName);
        return encode(binary);
    }

    /**
     * UTF-8编码格式的URL Base64编码（+/为-_）
     * @param plainText 编码文本
     * @return url编码
     */
    public static String encodeUrl(String plainText) {
        return encodeUrl(plainText, CharEncoding.UTF_8);
    }

    /**
     * 指定编码格式的URL Base64编码（+/为-_）
     * @param plainText 编码文本
     * @param charName 编码格式
     * @return url编码
     */
    public static String encodeUrl(String plainText, String charName) {
        byte[] binary = StringUtils.stringToBytes(plainText, charName);
        return encodeUrl(binary);
    }

    /**
     * 字节数组的Base64编码
     * @param binary 编码字节数组
     * @return base64编码
     */
    public static String encodeUrl(byte[] binary){
        return Base64.encodeBase64URLSafeString(binary);
    }

    /**
     * UTF-8格式的Base64解码
     * @param plainText 解码文本
     * @return base64解码
     */
    public static byte[] decode(String plainText) {
        return decode(plainText, CharEncoding.UTF_8);
    }


    /**
     * 指定编码格式的Base64解码
     * @param plainText 解码文本
     * @param charsetName 编码格式
     * @return base64解码
     */
    public static byte[] decode(String plainText, String charsetName){
        byte[] bytes = StringUtils.stringToBytes(plainText, charsetName);
        return decode(bytes);
    }

    /**
     * 字节数组的Base64解码
     * @param binary 解码字节数组
     * @return base64解码
     */
    public static byte[] decode(byte[] binary) {
        return Base64.decodeBase64(binary);
    }
}
