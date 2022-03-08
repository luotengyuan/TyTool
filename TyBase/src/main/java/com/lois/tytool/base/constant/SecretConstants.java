package com.lois.tytool.base.constant;

/**
 * @Description 加解密常量
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class SecretConstants {
    /**
     * 散列算法 SHA
     */
    public static final String SHA_KEY= "SHA";
    public static final String SHA_KEY_224 = "SHA-224";
    public static final String SHA_KEY_256 = "SHA-256";
    public static final String SHA_KEY_384 = "SHA-384";
    public static final String SHA_KEY_512 = "SHA-512";


    public static final String HMAC_SHA1 = "HmacSHA1";
    public static final String HMAC_SHA224 = "HmacSHA224";
    public static final String HMAC_SHA256 = "HmacSHA256";
    public static final String HMAC_SHA384 = "HmacSHA384";
    public static final String HMAC_SHA512 = "HmacSHA512";
    /**
     * AES
     */
    public static final String AES_KEY_ALGORITHM = "AES";
    public static final String SHA_ALGORITHM = "SHA1PRNG";
    public static final String PROVIDER_NAME = "SUN";
    /**
     * DES
     */
    public static final String DES_KEY_ALGORITHM = "DES";
    public static final String DES3_KEY_ALGORITHM = "DESede";
    /**
     * MD5
     */
    public static final String MD5_KEY_ALGORITHM = "MD5";
    public static final String HMAC_MD5_KEY_ALGORITHM = "HmacMD5";
    /**
     * RSA
     */
    public static final String RSA_KEY_ALGORITHM = "RSA";
    /**
     * RSA 数字签名签名/验证算法
     */
    public static final String SIGNATURE_ALGORITHM_MD5 = "MD5withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA1 = "SHA1withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA256 = "SHA256withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA384 = "SHA384withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA512 = "SHA512withRSA";
    /**
     * RSA密钥长度，RSA算法的默认密钥长度是1024密钥长度必须是64的倍数，在512到65536位之间
     */
    public static final int KEY_SIZE_512 = 512;
    public static final int KEY_SIZE_1024 = 1024;
    public static final int KEY_SIZE_2048 = 2048;
    public static final int KEY_SIZE_4096 = 4096;


    public static final String PUBLIC_KEY = "publicKeyString";
    public static final String PRIVATE_KEY = "privateKeyString";
}
