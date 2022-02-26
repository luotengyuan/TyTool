package com.lois.tytool.basej.secert.symmetric;

import com.lois.tytool.basej.constant.SecretConstants;
import com.lois.tytool.basej.secert.BaseSecret;
import com.lois.tytool.basej.secert.enumeration.AlgorithmMode;
import com.lois.tytool.basej.secert.enumeration.Padding;
import com.lois.tytool.basej.util.HexUtils;
import com.lois.tytool.basej.string.StringUtils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 对称加解密工具
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public final class SymmetricEncryptUtils {
    /**
     * DES加密最小长度
     */
    private static final int DES_MINLENGTH = 8;
    /**
     * 偏移量最小长度
     */
    private static final int IV_MINLENGTH = 24;

    private SymmetricEncryptUtils() {
    }

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

    /**
     * 加密
     * @param plainText 要加密的文本
     * @param key 密码
     * @param algorithmType 加密方式（DES\3DES）
     * @param algorithmMode 加密模式（ECB\CBC\CTR\OFB\CFB）
     * @param padding 填充模式（pkcs5padding\pkcs7padding\zeropadding\iso1016\ansix923）
     * @param iv 偏移量
     * @param charsetName 字符集
     * @return 密文
     */
    protected static String encrypt(String plainText, String key, String algorithmType, AlgorithmMode algorithmMode, Padding padding, String iv, String charsetName){
        if (StringUtils.isBlank(key) || key.length() < DES_MINLENGTH) {
            throw new IllegalArgumentException("加密失败，密钥不能小于8位！");
        }else if(algorithmType.equals(SecretConstants.DES3_KEY_ALGORITHM) && key.length() < IV_MINLENGTH){
            throw new IllegalArgumentException("加密失败，3DES加密密钥不能小于24位！");
        }
        if (StringUtils.isBlank(plainText)){
            throw new IllegalArgumentException("加密原文不能为空！");
        }
        byte[] binary = StringUtils.stringToBytes(plainText, charsetName);
        byte[] des;
        if(AlgorithmMode.ECB.equals(algorithmMode)){
            des = des(binary, algorithmType, key, Cipher.ENCRYPT_MODE, algorithmMode, padding);
        }else{
            if(StringUtils.isBlank(iv) || (iv.length() % DES_MINLENGTH != 0)){
                throw new IllegalArgumentException("偏移量长度必须为8的倍数！");
            }
            des = des(binary, algorithmType, key, Cipher.ENCRYPT_MODE, algorithmMode, padding, iv);
        }
        return HexUtils.bytesToHexString(des);
    }

    /**
     * 解密
     * @param plainText 要解密的文本
     * @param key 密码
     * @param algorithmType 加密方式（DES\3DES）
     * @param algorithmMode 解密模式（ECB\CBC\CTR\OFB\CFB）
     * @param padding 填充模式（pkcs5padding\pkcs7padding\zeropadding\iso1016\ansix923）
     * @param iv 偏移量
     * @param charsetName 字符集
     * @return 明文
     */
    protected static String decrypt(String plainText, String  key, String algorithmType, AlgorithmMode algorithmMode, Padding padding, String iv, String charsetName){
        //DES加解密密钥不能小于8位
        if (StringUtils.isBlank(key) || key.length() < DES_MINLENGTH) {
            throw new IllegalArgumentException("解密失败，密钥不能小于8位！");
            //DES加解密密钥不能小于24位
        }else if(algorithmType.equals(SecretConstants.DES3_KEY_ALGORITHM) && key.length() < IV_MINLENGTH){
            throw new IllegalArgumentException("解密失败，3DES解密密钥不能小于24位！");
        }
        if (StringUtils.isBlank(plainText)){
            throw new IllegalArgumentException("解密原文不能为空！");
        }
        byte[] binary = HexUtils.hexStringToBytes(plainText);
        byte[] des;
        if(AlgorithmMode.ECB.equals(algorithmMode)){
            des = des(binary, algorithmType, key, Cipher.DECRYPT_MODE, algorithmMode, padding);
        } else if (StringUtils.isBlank(iv) || (iv.length() % DES_MINLENGTH != 0)) {
            throw new IllegalArgumentException("偏移量长度必须为8的倍数！");
        } else {
            des = des(binary, algorithmType, key, Cipher.DECRYPT_MODE, algorithmMode, padding, iv);
        }
        return StringUtils.bytesToString(des, charsetName);
    }

    /**
     * DES/3DES加/解密
     * @param binary 要加/解密的文本
     * @param algorithmType 加密方式（DES\3DES\AES）
     * @param key 密码
     * @param cipherType 加密/解密
     * @param algorithmMode 加解密模式（ECB\CBC\CTR\OFB\CFB）
     * @param paddType 填充模式（pkcs5padding\pkcs7padding\zeropadding\iso1016\ansix923）
     * @param iv 偏移量
     * @return 明文/密文
     */
    private static byte[] des(byte[] binary, String algorithmType, String  key, int cipherType, AlgorithmMode algorithmMode, Padding paddType, String iv){
        if (StringUtils.isBlank(algorithmType)) {
            throw new IllegalArgumentException("加密方式不能为空");
        }
        SecretKey secretKey = BaseSecret.getSecretKeyBySecretKeyFactory(key, algorithmType);
        String type = getAlgorithmType(algorithmMode, paddType, algorithmType);
        SecureRandom secureRandom = BaseSecret.getSecureRandom(SecretConstants.PROVIDER_NAME, SecretConstants.SHA_ALGORITHM, key);

        byte[] bytes = StringUtils.stringToBytes(iv);
        return BaseSecret.getSecretByCipher(type, bytes, cipherType, secretKey, secureRandom, binary);
    }

    /**
     *
     * @param binary
     * @param algorithmType
     * @param key
     * @param cipherType
     * @param algorithmMode
     * @param paddType
     * @return
     */
    private static byte[] des(byte[] binary, String algorithmType, String  key, int cipherType, AlgorithmMode algorithmMode, Padding paddType){
        if (StringUtils.isBlank(algorithmType)) {
            throw new IllegalArgumentException("加密方式不能为空");
        }
        SecretKey secretKey = BaseSecret.getSecretKeyBySecretKeyFactory(key, algorithmType);
        String type = getAlgorithmType(algorithmMode, paddType, algorithmType);

        SecureRandom secureRandom = BaseSecret.getSecureRandom(SecretConstants.PROVIDER_NAME, SecretConstants.SHA_ALGORITHM, key);

        return BaseSecret.getSecretByCipher(type, null, cipherType, secretKey, secureRandom, binary);
    }


    /**
     * AES加解密 数据块问题暂时作废
     * @param algorithmMode 加解密模式（CBC\CTR\OFB\CFB）
     * @param padding 填充模式（pkcs5padding\pkcs7padding\zeropadding\iso1016\ansix923）
     * @param iv 偏移量
     * @param cipherType 加/解密
     * @param password 密码
     * @param keyLength 数据块（128、192、256）
     * @param src 加解密对象
     * @return 密文/明文
     */
    protected static byte[] aes(AlgorithmMode algorithmMode, Padding padding, String iv, int cipherType, String password, int keyLength, byte[] src){
        String algorithmType = getAlgorithmType(algorithmMode, padding, SecretConstants.AES_KEY_ALGORITHM);
        SecretKeySpec key = getSecretKeySpec(password, padding, algorithmType);

        byte[] bytes = StringUtils.stringToBytes(iv);

        return BaseSecret.getSecretByCipher(algorithmType, bytes, cipherType, key, null, src);
    }

    /**
     * AES加解密
     * @param algorithmMode 加解密模式（ECB）
     * @param padding 填充模式（pkcs5padding\pkcs7padding\zeropadding\iso1016\ansix923）
     * @param cipherType 加/解密
     * @param password 密码
     * @param keyLength 数据块（128、192、256）
     * @param src 加解密对象
     * @return 密文/明文
     */
    protected static byte[] aes(AlgorithmMode algorithmMode, Padding padding, int cipherType, String password, int keyLength, byte[] src){
        String algorithmType = getAlgorithmType(algorithmMode, padding, SecretConstants.AES_KEY_ALGORITHM);
        SecretKeySpec key = getSecretKeySpec(password, padding, algorithmType);

        return BaseSecret.getSecretByCipher(algorithmType, null, cipherType, key, null, src);
    }

    /**
     * 获取AES密钥
     * @param password 原始密钥
     * @param padding 填充模式
     * @param algorithmType 加解密类型
     * @return
     */
    private static SecretKeySpec getSecretKeySpec(String password, Padding padding, String algorithmType){
        byte[] passwordBytes = StringUtils.stringToBytes(password);
        //NOPADDING和ZEROPADDING填充模式使用“AES”生成密钥，其他使用完整的加解密声明
        if (Padding.NOPADDING.equals(padding) || Padding.ZEROPADDING.equals(padding)) {
            return new SecretKeySpec(passwordBytes, SecretConstants.AES_KEY_ALGORITHM);
        }else{
            return new SecretKeySpec(passwordBytes, algorithmType);
        }
    }
    /**
     * AES加解密 数据块问题暂时作废
     * @param algorithmMode 加解密模式（ECB\CBC\CTR\OFB\CFB）
     * @param padding 填充模式（pkcs5padding\pkcs7padding\zeropadding\iso1016\ansix923）
     * @param iv 偏移量
     * @param cipherType 加/解密
     * @param password 密码
     * @param src 加解密对象
     * @return 密文/明文
     */
    protected static byte[] aes(AlgorithmMode algorithmMode, Padding padding, String iv, int cipherType, String password, byte[] src){
        int defaultLength = 12;
        if(AlgorithmMode.ECB.equals(algorithmMode)){
            return aes(algorithmMode, padding, cipherType, password, defaultLength, src);
        }
        return aes(algorithmMode, padding, iv, cipherType, password, defaultLength, src);
    }

    /**
     * 密钥生成，待定
     * @param providerType 提供类型
     * @param secureType 加密方式
     * @param password 密码
     * @param algorithmType 加密方式
     * @param keyLength  数据块（128、192、256）
     * @return 密钥
     */
    private static byte[] getKey(String providerType, String secureType, String password, String algorithmType, int keyLength){
        SecureRandom secureRandom = BaseSecret.getSecureRandom(providerType, secureType, password);
        SecretKey secretKey = BaseSecret.getSecretKeyByKeyGenerator(algorithmType, password, keyLength, secureRandom);
        return secretKey.getEncoded();
    }

    /**
     * 组装加解密模式
     * @param algorithmMode
     * @param paddType
     * @param algorithmType
     * @return
     */
    private static String getAlgorithmType(AlgorithmMode algorithmMode, Padding paddType, String algorithmType){
        String mode = getAlgorithmMode(algorithmMode);

        String padding = getPadding(paddType);
        return algorithmType + "/" + mode + "/" + padding;
    }
    /**
     * 获取加解密模式
     * @param algorithmMode
     * @return
     */
    private static String getAlgorithmMode(AlgorithmMode algorithmMode){
        String mode = "ECB";
        switch (algorithmMode){
            case CBC:
                mode = "CBC";
                break;
            case CFB:
                mode = "CFB";
                break;
            case CTR:
                mode = "CTR";
                break;
            case OFB:
                mode = "OFB";
                break;
            case ECB:
            default:
                break;
        }
        return mode;
    }

    /**
     * 获取填充模式
     * @param paddType
     * @return
     */
    private static String getPadding(Padding paddType){
        String padding = "PKCS5PADDING";
        switch (paddType){
//            case ANSIX923:
//                padding = "ANSIX923";
//                break;
            case ISO10126:
                padding = "ISO10126PADDING";
                break;
            case NOPADDING:
                padding = "NOPADDING";
                break;
            case ZEROPADDING:
                padding = "ZEROPADDING";
                break;
            case PKCS5PADDING:
                padding = "PKCS5PADDING";
                break;
            case PKCS7PADDING:
                padding = "PKCS7PADDING";
                break;
            default:
                break;
        }
        return padding;
    }
}
