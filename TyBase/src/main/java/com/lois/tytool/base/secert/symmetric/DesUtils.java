package com.lois.tytool.base.secert.symmetric;

import com.lois.tytool.base.constant.SecretConstants;
import com.lois.tytool.base.secert.enumeration.AlgorithmMode;
import com.lois.tytool.base.secert.enumeration.Padding;
import com.lois.tytool.base.string.StringUtils;

import org.apache.commons.codec.CharEncoding;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Key;
import java.security.Security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES、3DES加解密工具
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class DesUtils {

    /**
     * 返回可逆算法DES的密钥
     *
     * @param key 前8字节将被用来生成密钥。
     * @return 生成的密钥
     * @throws Exception
     */
    public static Key getDESKey(byte[] key) throws Exception {
        DESKeySpec des = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        return keyFactory.generateSecret(des);
    }

    /**
     *
     */
    private static final int DES3_MINLENGTH = 24;
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
    private DesUtils(){

    }
    /**
     * DES/CBC加密模式
     * @param data 原文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 密文
     */
    public static String encryptCbc(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.encrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.CBC, padding, iv, CharEncoding.UTF_8);
    }
    /**
     * 3DES/CBC加密模式
     * @param data 原文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 密文
     */
    public static String encrypt3DesCbc(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        if(StringUtils.isBlank(password) || password.length() < DES3_MINLENGTH){
            throw new IllegalArgumentException("3DES加密密钥长度不能少于24位");
        }
        return SymmetricEncryptUtils.encrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES3_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.CBC, padding, iv, CharEncoding.UTF_8);
    }

    /**
     * DES/CBC解密模式
     * @param data 密文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 明文
     */
    public static String decryptCbc(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.decrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.CBC, padding, iv, CharEncoding.UTF_8);
    }
    /**
     * DES/CBC解密模式
     * @param data 密文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 明文
     */
    public static String decrypt3DesCbc(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.decrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES3_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.CBC, padding, iv, CharEncoding.UTF_8);
    }
    /**
     * DES/CFB加密模式
     * @param data 原文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 密文
     */
    public static String encryptCfb(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.encrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.CFB, padding, iv, CharEncoding.UTF_8);
    }
    /**
     * 3DES/CFB加密模式
     * @param data 原文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 密文
     */
    public static String encrypt3DesCfb(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.encrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES3_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.CFB, padding, iv, CharEncoding.UTF_8);
    }

    /**
     * DES/CFB解密模式
     * @param data 密文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 明文
     */
    public static String decryptCfb(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.decrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.CFB, padding, iv, CharEncoding.UTF_8);
    }
    /**
     * 3DES/CFB解密模式
     * @param data 密文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 明文
     */
    public static String decrypt3DesCfb(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.decrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES3_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.CFB, padding, iv, CharEncoding.UTF_8);
    }
    /**
     * DES/OFB加密模式
     * @param data 原文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 密文
     */
    public static String encryptOfb(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.encrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.OFB, padding, iv, CharEncoding.UTF_8);
    }
    /**
     * 3DES/OFB加密模式
     * @param data 原文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 密文
     */
    public static String encrypt3DesOfb(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.encrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES3_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.OFB, padding, iv, CharEncoding.UTF_8);
    }

    /**
     * DES/OFB解密模式
     * @param data 密文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 明文
     */
    public static String decryptOfb(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.decrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.OFB, padding, iv, CharEncoding.UTF_8);
    }
    /**
     * 3DES/OFB解密模式
     * @param data 密文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 明文
     */
    public static String decrypt3DesOfb(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.decrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES3_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.OFB, padding, iv, CharEncoding.UTF_8);
    }
    /**
     * DES/CTR加密模式
     * @param data 原文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 密文
     */
    public static String encryptCtr(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.encrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.CTR, padding, iv, CharEncoding.UTF_8);
    }
    /**
     * 3DES/CTR加密模式
     * @param data 原文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 密文
     */
    public static String encrypt3DesCtr(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.encrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES3_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.CTR, padding, iv, CharEncoding.UTF_8);
    }

    /**
     * DES/CTR解密模式
     * @param data 密文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 明文
     */
    public static String decryptCtr(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.decrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.CTR, padding, iv, CharEncoding.UTF_8);
    }

    /**
     * 3DES/CTR解密模式
     * @param data 密文
     * @param password 密钥
     * @param iv 偏移量
     * @param padding 填充模式
     * @return 明文
     */
    public static String decrypt3DesCtr(String data, String password, String iv, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.decrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES3_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.CTR, padding, iv, CharEncoding.UTF_8);
    }

    /**
     * DES/ECB加密模式
     * @param data 原文
     * @param password 密钥
     * @param padding 填充模式
     * @return 密文
     */
    public static String encryptEcb(String data, String password, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.encrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.ECB, padding, null, CharEncoding.UTF_8);
    }
    /**
     * 3DES/ECB加密模式
     * @param data 原文
     * @param password 密钥
     * @param padding 填充模式
     * @return 密文
     */
    public static String encrypt3DesEcb(String data, String password, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.encrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES3_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.ECB, padding, null, CharEncoding.UTF_8);
    }

    /**
     * DES/ECB解密模式
     * @param data 密文
     * @param password 密钥
     * @param padding 填充模式
     * @return 明文
     */
    public static String decryptEcb(String data, String password, com.lois.tytool.base.secert.enumeration.Padding padding) {
        return SymmetricEncryptUtils.decrypt(data, password, com.lois.tytool.base.constant.SecretConstants.DES_KEY_ALGORITHM, com.lois.tytool.base.secert.enumeration.AlgorithmMode.ECB, padding, null, CharEncoding.UTF_8);
    }
    /**
     * 3DES/ECB解密模式
     * @param data 密文
     * @param password 密钥
     * @param padding 填充模式
     * @return 明文
     */
    public static String decrypt3DesEcb(String data, String password, Padding padding) {
        return SymmetricEncryptUtils.decrypt(data, password, SecretConstants.DES3_KEY_ALGORITHM, AlgorithmMode.ECB, padding, null, CharEncoding.UTF_8);
    }

}