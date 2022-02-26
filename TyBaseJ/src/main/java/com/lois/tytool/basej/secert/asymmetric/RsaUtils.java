package com.lois.tytool.basej.secert.asymmetric;

import com.lois.tytool.basej.constant.SecretConstants;
import com.lois.tytool.basej.secert.Base64Utils;
import com.lois.tytool.basej.secert.BaseSecret;
import com.lois.tytool.basej.secert.enumeration.RsaKeySize;
import com.lois.tytool.basej.secert.enumeration.RsaSignAlgorithm;
import com.lois.tytool.basej.string.StringUtils;

import org.apache.commons.net.util.Base64;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.Cipher;

/**
 * RSA加密和解密工具
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public final class RsaUtils {

    private RsaUtils(){

    }

    /**
     * 生成RSA算法密钥对，key 长度默认1024
     * @return 密钥对
     */
    public static com.lois.tytool.basej.secert.asymmetric.KeyPairManagement initKey(){
        return initKey(RsaKeySize.KEY_SIZE_1024);
    }
    /**
     * 生成RSA算法密钥对
     * @param keySize 密钥长度
     * @return 密钥对
     */
    public static com.lois.tytool.basej.secert.asymmetric.KeyPairManagement initKey(RsaKeySize keySize){
        SecureRandom initSeedSh = BaseSecret.getSecureRandom(SecretConstants.PROVIDER_NAME, SecretConstants.SHA_ALGORITHM, "initSeedSh");
        return initKey(keySize, initSeedSh);
    }
    /**
     * 生成RSA算法密钥对
     * @param keySize 密钥长度
     * @param secrand 安全随机数
     * @return 密钥对
     */
    public static com.lois.tytool.basej.secert.asymmetric.KeyPairManagement initKey(RsaKeySize keySize, SecureRandom secrand){
        KeyPairGenerator keygen = null;
        try {
            keygen = KeyPairGenerator.getInstance(SecretConstants.RSA_KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        int size = getKeySize(keySize);
        //初始化密钥生成器
        keygen.initialize(size, secrand);
        KeyPair keys = keygen.genKeyPair();

        byte[] pubKey = keys.getPublic().getEncoded();
        byte[] priKey = keys.getPrivate().getEncoded();
        com.lois.tytool.basej.secert.asymmetric.KeyPairManagement keyPairManagement = new KeyPairManagement();
        keyPairManagement.setPublicKey(pubKey);
        keyPairManagement.setPrivateKey(priKey);
        return keyPairManagement;
    }

    /**
     * 获取keysize长度
     * @param keySize keysize类型
     * @return 返回keysize长度，默认1024
     */
    private static int getKeySize(RsaKeySize keySize) {
        switch (keySize) {
            case KEY_SIZE_512:
                return SecretConstants.KEY_SIZE_512;
            case KEY_SIZE_2048:
                return SecretConstants.KEY_SIZE_2048;
            case KEY_SIZE_4096:
                return SecretConstants.KEY_SIZE_4096;
            case KEY_SIZE_1024:
            default:
                return SecretConstants.KEY_SIZE_1024;
        }
    }


    /**
     * 使用公钥加密，默认UTF-8编码
     * @param plainText 明文
     * @param publicKey 公钥
     * @return 密文（base64格式）
     */
    public static String encryptByPubliceKey(String plainText, byte[] publicKey){
        byte[] enSign = encryptByPubliceKey(StringUtils.stringToBytes(plainText), publicKey);
        return Base64.encodeBase64String(enSign);
    }

    /**
     * 使用公钥加密，默认UTF-8编码
     * @param plainText 明文
     * @param publicKey 公钥（base64格式）
     * @return 密文（base64格式）
     */
    public static String encryptByPubliceKey(String plainText, String publicKey){
        return encryptByPubliceKey(plainText, Base64Utils.decode(publicKey));
    }

    /**
     * 公钥加密
     * @param plainText 明文字节数组
     * @param pubKey 公钥字节数组
     * @return 密文字节数组
     */
    public static byte[] encryptByPubliceKey(byte[] plainText, byte[] pubKey){
        PublicKey publicKey = (PublicKey) BaseSecret.createKeyFactory(SecretConstants.RSA_KEY_ALGORITHM, true, pubKey);
        return BaseSecret.getSecretByCipher(SecretConstants.RSA_KEY_ALGORITHM, Cipher.ENCRYPT_MODE, publicKey, plainText);
    }

    /**
     * 公钥加密
     * @param plainText 明文字节数组
     * @param publicKey 公钥（base64格式）
     * @return 密文字节数组
     */
    public static byte[] encryptByPubliceKey(byte[] plainText, String publicKey){
        return encryptByPubliceKey(plainText, Base64Utils.decode(publicKey));
    }



    /**
     * 私钥加密，默认UTF-8编码
     * @param plainText 明文
     * @param privateKey 私钥字节数组
     * @return 密文（base64格式）
     */
    public static String encryptByPrivateKey(String plainText, byte[] privateKey) {
        byte[] enSign = encryptByPrivateKey(StringUtils.stringToBytes(plainText), privateKey);
        return Base64Utils.encode(enSign);
    }

    /**
     * 私钥加密，默认UTF-8编码
     * @param plainText 明文
     * @param privateKey 私钥（base64格式）
     * @return 密文（base64格式）
     */
    public static String encryptByPrivateKey(String plainText, String privateKey) {
        return encryptByPrivateKey(plainText, Base64Utils.decode(privateKey));
    }

    /**
     * 私钥加密
     * @param plainText 明文字节数组
     * @param privateKey 私钥字节数组
     * @return 密文
     */
    public static byte[] encryptByPrivateKey(byte[] plainText, byte[] privateKey) {
        PrivateKey priKey = (PrivateKey) BaseSecret.createKeyFactory(SecretConstants.RSA_KEY_ALGORITHM, false, privateKey);
        return BaseSecret.getSecretByCipher(SecretConstants.RSA_KEY_ALGORITHM, Cipher.ENCRYPT_MODE, priKey, plainText);
    }

    /**
     * 私钥加密
     * @param plainText 明文字节数组
     * @param privateKey 私钥（base64格式）
     * @return 密文字节数组
     */
    public static byte[] encryptByPrivateKey(byte[] plainText, String privateKey) {
        return encryptByPrivateKey(plainText, Base64Utils.decode(privateKey));
    }


    /**
     * 公钥解密
     * @param cipher 密文字节数组
     * @param publicKey 公钥字节数组
     * @return 明文字节数组
     */
    public static byte[] decryptByPublicKey(byte[] cipher, byte[] publicKey) {
        PublicKey  pubKey  = (PublicKey) BaseSecret.createKeyFactory(SecretConstants.RSA_KEY_ALGORITHM, true, publicKey);
        return BaseSecret.getSecretByCipher(SecretConstants.RSA_KEY_ALGORITHM, Cipher.DECRYPT_MODE, pubKey, cipher);
    }

    /**
     * 公钥解密
     * @param cipher 密文字节数组
     * @param publicKey 公钥（base64格式）
     * @return 明文字节数组
     */
    public static byte[] decryptByPublicKey(byte[] cipher, String publicKey) {
        byte[] publicByte = Base64Utils.decode(publicKey);
        return decryptByPublicKey(cipher, publicByte);
    }

    /**
     * 公钥解密
     * @param cipher 密文（base64格式）
     * @param publicKey 公钥字节数组
     * @return 明文（UTF-8编码）
     */
    public static String decryptByPublicKey(String cipher, byte[] publicKey) {
        byte[] design = decryptByPublicKey(Base64Utils.decode(cipher), publicKey);
        return StringUtils.bytesToString(design);
    }

    /**
     * 公钥解密
     * @param cipher 密文（base64格式）
     * @param publicKey 公钥（base64格式）
     * @return 明文（UTF-8编码）
     */
    public static String decryptByPublicKey(String cipher, String publicKey) {
        return decryptByPublicKey(cipher, Base64Utils.decode(publicKey));
    }


    /**
     * 私钥解密
     * @param cipher 密文字节数组
     * @param privateKey 私钥字节数组
     * @return 明文字节数组
     */
    public static byte[] decryptByPrivateKey(byte[] cipher, byte[] privateKey) {
        PrivateKey priKey  = (PrivateKey) BaseSecret.createKeyFactory(SecretConstants.RSA_KEY_ALGORITHM, false, privateKey);
        return BaseSecret.getSecretByCipher(SecretConstants.RSA_KEY_ALGORITHM, Cipher.DECRYPT_MODE, priKey, cipher);
    }

    /**
     * 私钥解密
     * @param cipher 密文字节数组
     * @param privateKey 私钥（base64格式）
     * @return 明文字节数组
     */
    public static byte[] decryptByPrivateKey(byte[] cipher, String privateKey) {
        return decryptByPrivateKey(cipher, Base64Utils.decode(privateKey));
    }


    /**
     * 私钥解密
     * @param cipher 密文（base64格式）
     * @param privateKey 私钥字节数组
     * @return 明文（UTF-8编码）
     */
    public static String decryptByPrivateKey(String cipher, byte[] privateKey) {
        byte[] design = decryptByPrivateKey(Base64Utils.decode(cipher), privateKey);
        return StringUtils.bytesToString(design);
    }

    /**
     * 私钥解密
     * @param cipher 密文（base64格式）
     * @param privateKey 私钥（base64格式）
     * @return 明文（UTF-8编码）
     */
    public static String decryptByPrivateKey(String cipher, String privateKey) {
        return decryptByPrivateKey(cipher, Base64Utils.decode(privateKey));
    }


    /**
     * RSA签名，签名算法默认采用SHA1withRSA
     * @param data 签名内容字节数组
     * @param privateKey 私钥字节数组
     * @return 签名数据
     */
    public static String sign(byte[] data, byte[] privateKey) {
        return sign(data, privateKey, RsaSignAlgorithm.SIGNATURE_ALGORITHM_SHA1);
    }

    /**
     * RSA签名
     * @param data 签名内容字节数组
     * @param privateKey 私钥字节数组
     * @param signAlgorithm 签名算法
     * @return 签名数据
     */
    public static String sign(byte[] data, byte[] privateKey, RsaSignAlgorithm signAlgorithm) {
        PrivateKey priKey = (PrivateKey) BaseSecret.createKeyFactory(SecretConstants.RSA_KEY_ALGORITHM, false, privateKey);
        String signatureAlgorithm = getRsaSignAlgorithm(signAlgorithm);
        // 实例化Signature
        Signature signature = null;
        try {
            signature = Signature.getInstance(signatureAlgorithm);
            // 初始化Signature
            signature.initSign(priKey);
            // 更新
            signature.update(data);
            byte[] result = signature.sign();
            return Base64Utils.encode(result);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        } catch (SignatureException e) {
            throw new IllegalArgumentException(e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * RSA签名，签名算法默认采用SHA1withRSA
     * @param data 签名内容字节数组
     * @param privateKey 私钥（base64字节数组）
     * @return 签名数据
     */
    public static String sign(byte[] data, String privateKey) {
        return sign(data, Base64Utils.decode(privateKey));
    }


    /**
     * RSA签名，签名算法默认采用SHA1withRSA
     * @param data 签名内容
     * @param privateKey 私钥字节数组
     * @return 签名数据
     */
    public static String sign(String data, byte[] privateKey) {
        return sign(StringUtils.stringToBytes(data), privateKey);
    }

    /**
     * RSA签名，签名算法默认采用SHA1withRSA
     * @param data 签名内容
     * @param privateKey 私钥（base64格式）
     * @return 签名数据
     */
    public static String sign(String data, String privateKey) {
        return sign(StringUtils.stringToBytes(data), Base64Utils.decode(privateKey));
    }


    /**
     * RSA校验数字签名，签名算法默认采用SHA1withRSA
     * @param data 原始数据
     * @param sign 数字签名
     * @param publicKey 公钥
     * @return 校验成功返回true，失败返回false
     */
    public static boolean verify(byte[] data, byte[] sign, byte[] publicKey) {
        boolean result = verify(data, sign, RsaSignAlgorithm.SIGNATURE_ALGORITHM_SHA1, publicKey);
        return result;
    }

    /**
     * RSA校验数字签名
     * @param data 原始数据
     * @param sign 数字签名
     * @param signAlgorithm 签名算法
     * @param publicKey 公钥
     * @return 校验成功返回true，失败返回false
     */
    public static boolean verify(byte[] data, byte[] sign, RsaSignAlgorithm signAlgorithm, byte[] publicKey) {
        PublicKey pubKey = (PublicKey) BaseSecret.createKeyFactory(SecretConstants.RSA_KEY_ALGORITHM, true, publicKey);
        String signatureAlgorithm = getRsaSignAlgorithm(signAlgorithm);
        Signature signature = null;
        try {
            signature = Signature.getInstance(signatureAlgorithm);
            // 初始化Signature
            signature.initVerify(pubKey);
            // 更新
            signature.update(data);
            // 验证
            return signature.verify(sign);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        } catch (SignatureException e) {
            throw new IllegalArgumentException(e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * RSA校验数字签名，签名算法默认采用SHA1withRSA
     * @param data 原始数据
     * @param sign 数字签名
     * @param publicKey 公钥（base64格式）
     * @return 校验成功返回true，失败返回false
     */
    public static boolean verify(byte[] data, byte[] sign, String publicKey) {
        return verify(data, sign, Base64Utils.decode(publicKey));
    }

    /**
     * RSA校验数字签名，签名算法默认采用SHA1withRSA
     * @param dataStr 原始数据
     * @param signStr 签名（base64格式）
     * @param publicKey 公钥字节数组
     * @return 校验成功返回true，失败返回false
     */
    public static boolean verify(String dataStr, String signStr, byte[] publicKey) {
        byte[] sign = Base64Utils.decode(StringUtils.stringToBytes(signStr));
        return verify(StringUtils.stringToBytes(dataStr), sign, publicKey);
    }

    /**
     * RSA校验数字签名，签名算法默认采用SHA1withRSA
     * @param dataStr 原始数据
     * @param signStr 签名（base64格式）
     * @param publicKey 公钥（base64格式）
     * @return 校验成功返回true，失败返回false
     */
    public static boolean verify(String dataStr, String signStr, String publicKey) {
        return verify(dataStr, signStr, Base64Utils.decode(publicKey));
    }



    /**
     * 签名或校验的公共方法，统合是方便做异常处理
     * @param data 待校验数据
     * @param sign 签名
     * @param signatureAlgorithm 签名算法
     * @param privateKey 私钥
     * @param publicKey 公钥
     * @return 验名
     */
    private static byte[] signOrVerify(byte[] data, byte[] sign,String signatureAlgorithm, PrivateKey privateKey, PublicKey publicKey){
        // 实例化Signature
        Signature signature = null;
        byte[] result = {0};
        try {
            signature = Signature.getInstance(signatureAlgorithm);
            if(privateKey != null) {
                // 初始化Signature
                signature.initSign(privateKey);
                // 更新
                signature.update(data);
                result = signature.sign();
            } else if(publicKey != null) {
                // 初始化Signature
                signature.initVerify(publicKey);
                // 更新
                signature.update(data);
                // 验证
                if(!signature.verify(sign)) {
                    result = null;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        } catch (SignatureException e) {
            throw new IllegalArgumentException(e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
        return result;
    }


    /**
     * 根据签名算法类型，返回算法名称
     * 默认算法：SHA1withRSA
     * @param signAlgorithm 签名算法类型
     * @return 算法名称
     */
    private static String getRsaSignAlgorithm(RsaSignAlgorithm signAlgorithm) {
        switch (signAlgorithm) {
            case SIGNATURE_ALGORITHM_MD5:
                return SecretConstants.SIGNATURE_ALGORITHM_MD5;
            case SIGNATURE_ALGORITHM_SHA256:
                return SecretConstants.SIGNATURE_ALGORITHM_SHA256;
            case SIGNATURE_ALGORITHM_SHA384:
                return SecretConstants.SIGNATURE_ALGORITHM_SHA384;
            case SIGNATURE_ALGORITHM_SHA512:
                return SecretConstants.SIGNATURE_ALGORITHM_SHA512;
            case SIGNATURE_ALGORITHM_SHA1:
            default:
                return SecretConstants.SIGNATURE_ALGORITHM_SHA1;
        }
    }

}

