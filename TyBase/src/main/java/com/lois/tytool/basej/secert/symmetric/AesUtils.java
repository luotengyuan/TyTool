package com.lois.tytool.basej.secert.symmetric;

import com.lois.tytool.basej.secert.enumeration.AlgorithmMode;
import com.lois.tytool.basej.secert.enumeration.Padding;
import com.lois.tytool.basej.util.HexUtils;
import com.lois.tytool.basej.string.StringUtils;

import org.apache.commons.codec.CharEncoding;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

import javax.crypto.Cipher;

/**
 * AES 128位加解密工具
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class AesUtils {
    /**
     * 密钥最小长度
     */
    private static final int MINLENGTH = 16;

    static {
        //如果是PKCS7Padding填充方式，则必须加上下面这行
        Security.addProvider(new BouncyCastleProvider());
    }
    static {
        try {
            RemoveCryptographyRestrictions.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private AesUtils(){

    }
    /**
     * AES加密
     * @param plainText 待加密文本
     * @param password 密码
     * @param algorithmMode 加解密模式（ECB\CBC\CTR\OFB\CFB）
     * @param padding 填充模式（pkcs5padding\pkcs7padding\zeropadding\iso1016\ansix923）
     * @param iv 偏移量
     * @param charsetName 字符集
     * @return 密文
     */
    protected static String encrypt(String plainText, String password, AlgorithmMode algorithmMode, Padding padding, String iv, String charsetName) {
        if(StringUtils.isBlank(password) || password.length() < MINLENGTH){
            throw new IllegalArgumentException("密钥长度不能小于16位！");
        }
        if(!AlgorithmMode.ECB.equals(algorithmMode)){
            if(StringUtils.isBlank(iv) || iv.length() < MINLENGTH){
                throw new IllegalArgumentException("当前加密模式下偏移量长度不能小于16位！");
            }
        }
        byte[] bytes = StringUtils.stringToBytes(plainText, charsetName);
        byte[] result = SymmetricEncryptUtils.aes(algorithmMode, padding, iv, Cipher.ENCRYPT_MODE, password, bytes);
        return HexUtils.bytesToHexString(result);
    }

    /**
     * AES解密
     * @param encrypted 待解密的文本
     * @param password 密码
     * @param algorithmMode 加解密模式（ECB\CBC\CTR\OFB\CFB）
     * @param padding 填充模式（pkcs5padding\pkcs7padding\zeropadding\iso1016\ansix923）
     * @param iv 偏移量
     * @param charsetName 字符集
     * @return 明文
     */
    protected static String decrypt(String encrypted, String password, AlgorithmMode algorithmMode, Padding padding, String iv, String charsetName){
        if(StringUtils.isBlank(password) || password.length() < MINLENGTH){
            throw new IllegalArgumentException("密钥长度不能小于16");
        }
        byte[] encryptedBytes = HexUtils.hexStringToBytes(encrypted);
        byte[] result = SymmetricEncryptUtils.aes(algorithmMode, padding, iv, Cipher.DECRYPT_MODE, password, encryptedBytes);
        //二进制转为字符串
        return StringUtils.bytesToString(result, charsetName);
    }
    /**
     * DES/CBC加密模式
     * @param data 原文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 密文
     */
    public static String encryptCbc(String data, String password, String iv, Padding padding) {
        return encrypt(data, password, AlgorithmMode.CBC, padding, iv, CharEncoding.UTF_8);
    }

    /**
     * DES/CBC解密模式
     * @param data 密文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 明文
     */
    public static String decryptCbc(String data, String password, String iv, Padding padding) {
        return decrypt(data, password, AlgorithmMode.CBC, padding, iv, CharEncoding.UTF_8);
    }
    /**
     * DES/CFB加密模式
     * @param data 原文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 密文
     */
    public static String encryptCfb(String data, String password, String iv, Padding padding) {
        return encrypt(data, password, AlgorithmMode.CFB, padding, iv, CharEncoding.UTF_8);
    }

    /**
     * DES/CFB解密模式
     * @param data 密文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 明文
     */
    public static String decryptCfb(String data, String password, String iv, Padding padding) {
        return decrypt(data, password, AlgorithmMode.CFB, padding, iv, CharEncoding.UTF_8);
    }
    /**
     * DES/OFB加密模式
     * @param data 原文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 密文
     */
    public static String encryptOfb(String data, String password, String iv, Padding padding) {
        return encrypt(data, password, AlgorithmMode.OFB, padding, iv, CharEncoding.UTF_8);
    }

    /**
     * DES/OFB解密模式
     * @param data 密文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 明文
     */
    public static String decryptOfb(String data, String password, String iv, Padding padding) {
        return decrypt(data, password, AlgorithmMode.OFB, padding, iv, CharEncoding.UTF_8);
    }
    /**
     * DES/CTR加密模式
     * @param data 原文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 密文
     */
    public static String encryptCtr(String data, String password, String iv, Padding padding) {
        return encrypt(data, password, AlgorithmMode.CTR, padding, iv, CharEncoding.UTF_8);
    }

    /**
     * DES/CTR解密模式
     * @param data 密文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 明文
     */
    public static String decryptCtr(String data, String password, String iv, Padding padding) {
        return decrypt(data, password, AlgorithmMode.CTR, padding, iv, CharEncoding.UTF_8);
    }


    /**
     * DES/ECB加密模式
     * @param data 原文
     * @param password 密钥
     * @param padding 填充模式
     * @return 密文
     */
    public static String encryptEcb(String data, String password, Padding padding) {
        return encrypt(data, password, AlgorithmMode.ECB, padding, null, CharEncoding.UTF_8);
    }

    /**
     * DES/ECB解密模式
     * @param data 密文
     * @param password 密钥
     * @param padding 填充模式
     * @return 明文
     */
    public static String decryptEcb(String data, String password, Padding padding) {
        return decrypt(data, password, AlgorithmMode.ECB, padding, null, CharEncoding.UTF_8);
    }
}
