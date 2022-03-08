package com.lois.tytool.basej.secert.hash;

import com.lois.tytool.basej.constant.SecretConstants;
import com.lois.tytool.basej.secert.BaseSecret;
import com.lois.tytool.basej.secert.enumeration.HashType;
import com.lois.tytool.basej.secert.enumeration.HmacHashType;

/**
 * HASH算法加密工具
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public final class HashUtils {
    private HashUtils() {
    }

    /**
     * SHA1加密，默认UTF-8编码
     * @param plainText 明文
     * @return （16进制Hex字符串）Hash密文
     */
    public static String sha1(String plainText){
        return BaseSecret.getMessageDigest(plainText, null, SecretConstants.SHA_KEY);
    }

    /**
     * hash算法加密，支持SHA1、SHA224、SHA256、SHA384、SHA512算法
     * @param plainTest 明文字节数组编码
     * @param hashType 加密类型
     * @return 加密后的字节数组
     */
    public static byte[] sha(byte[] plainTest, HashType hashType) {
        String hash = getHashType(hashType);
        byte[] messageDigest = BaseSecret.getMessageDigest(plainTest, null, hash);
        return messageDigest;
    }

    /**
     * SHA224加密，默认UTF-8编码
     * @param plainText 明文
     * @return （16进制Hex字符串）Hash224密文
     */
    public static String sha224(String plainText){
        return BaseSecret.getMessageDigest(plainText, null, SecretConstants.SHA_KEY_224);
    }
    /**
     * SHA256加密，默认UTF-8编码
     * @param plainText 明文
     * @return （16进制Hex字符串）Hash256密文
     */
    public static String sha256(String plainText){
        return BaseSecret.getMessageDigest(plainText, null, SecretConstants.SHA_KEY_256);
    }
    /**
     * SHA384加密，默认UTF-8编码
     * @param plainText 明文
     * @return （16进制Hex字符串）Hash384密文
     */
    public static String sha384(String plainText){
        return BaseSecret.getMessageDigest(plainText, null, SecretConstants.SHA_KEY_384);
    }
    /**
     * SHA512加密，默认UTF-8编码
     * @param plainText 明文
     * @return （16进制Hex字符串）Hash512密文
     */
    public static String sha512(String plainText){
        return BaseSecret.getMessageDigest(plainText, null, SecretConstants.SHA_KEY_512);
    }

    /**
     * 使用HmacSHA1加密算法进行加密（不可逆），默认UTF-8编码
     * @param plainText 明文
     * @param key 密钥
     * @return （16进制Hex字符串）hmacHash密文
     */
    public static String hmacSha1(String plainText, String key) {
        return BaseSecret.keyGeneratorMac(plainText, SecretConstants.HMAC_SHA1, key);
    }

    /**
     * 使用HmacSHA加密算法进行加密（不可逆），支持HmacHash1、HmacHash224、HmacHash256、HmacHash384、HmacHash512算法
     * @param plainText 明文字节数组
     * @param key 密钥字节数组
     * @param hmacHashType HmacHash算法
     * @return 加密的字节数组
     */
    public static byte[] hmacSha(byte[] plainText, byte[] key, HmacHashType hmacHashType) {
        String macType = getHmacHashType(hmacHashType);
        return BaseSecret.keyGeneratorMac(plainText, macType, key);
    }

    /**
     * 使用HmacSHA256加密算法进行加密（不可逆），默认UTF-8编码
     * @param plainText 明文
     * @param key 密钥
     * @return （16进制Hex字符串）hmacSha256密文
     */
    public static String hmacSha256(String plainText, String key) {
        return BaseSecret.keyGeneratorMac(plainText, SecretConstants.HMAC_SHA256, key);
    }

    /**
     * 使用HmacSHA512加密算法进行加密（不可逆），默认UTF-8编码
     * @param plainText 明文
     * @param key 密钥
     * @return （16进制Hex字符串）hmacSha512密文
     */
    public static String hmacSha512(String plainText, String key) {
        return BaseSecret.keyGeneratorMac(plainText, SecretConstants.HMAC_SHA512, key);
    }

    /**
     *  MD5加密
     * @param plainText 明文
     * @return （16进制Hex字符串）md5密文
     */
    public static String md5(String plainText) {
        return BaseSecret.getMessageDigest(plainText, null, SecretConstants.MD5_KEY_ALGORITHM);
    }


    /**
     * HmacMD5加密算法进行加密（不可逆），默认UTF-8编码
     * @param plainText 明文
     * @param key 秘钥
     * @return （16进制Hex字符串）hmacMd5密文
     */
    public static String hmacMd5(String plainText, String key) {
        return BaseSecret.keyGeneratorMac(plainText, SecretConstants.HMAC_MD5_KEY_ALGORITHM, key);
    }

    /**
     * 获取hash算法
     * @param hashType hash类型
     * @return hash算法，默认sha
     */
    private static String getHashType(HashType hashType) {
        switch (hashType) {
            case SHA224:
                return SecretConstants.SHA_KEY_224;
            case SHA256:
                return SecretConstants.SHA_KEY_256;
            case SHA384:
                return SecretConstants.SHA_KEY_384;
            case SHA512:
                return SecretConstants.SHA_KEY_512;
            case SHA:
            default:
                return SecretConstants.SHA_KEY;
        }
    }

    /**
     * 获取hmacsha算法
     * @param hmacHashType 算法类型
     * @return hmacsha算法，默认HmacSHA1算法
     */
    private static String getHmacHashType(HmacHashType hmacHashType) {
        switch (hmacHashType) {
            case HMAC_SHA256:
                return SecretConstants.HMAC_SHA256;
            case HMAC_SHA512:
                return SecretConstants.HMAC_SHA512;
            case HMAC_SHA:
            default:
                return SecretConstants.HMAC_SHA1;
        }
    }
}
