package com.lois.tytool.base.secert;

import com.lois.tytool.base.constant.SecretConstants;
import com.lois.tytool.base.debug.TyLog;
import com.lois.tytool.base.exception.EncryptionAndDecryptionException;
import com.lois.tytool.base.secert.enumeration.Padding;
import com.lois.tytool.base.string.StringUtils;
import com.lois.tytool.base.string.HexUtils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 作为加解密工具的基类，一个是简化代码，一个是统一做异常处理
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public final class BaseSecret {

    private static String AES = "AES";

    private BaseSecret() {
    }

    public static byte[] getSecretByCipher(String encodeType, int cipherType, Key secretKey, byte[] src) {
        return getSecretByCipher(encodeType,null, cipherType, secretKey, null, src);
    }
    /**
     * 通过Cipher进行加解密运算
     * @param encodeType 加密类型
     * @param iv 偏移量
     * @param cipherType cipher类型
     * @param secretKey 密匙
     * @param secureRandom 安全随机数
     * @param src 要加密的文本
     * @return 密文
     */
    public static byte[] getSecretByCipher(String encodeType, byte[] iv, int cipherType, Key secretKey, SecureRandom secureRandom, byte[] src) {
        SecretKeySpec spec = null;
        Cipher c1 = null;
        if(encodeType.contains(AES)) {
            spec = (SecretKeySpec) secretKey;
        }
        try {
            int index = -1;
            if(encodeType.toUpperCase().indexOf(Padding.ZEROPADDING.toString()) > 0){
                index = encodeType.toUpperCase().indexOf(Padding.ZEROPADDING.toString());
                encodeType = encodeType.substring(0, index) + Padding.NOPADDING;
            }
            //创建密码器
            c1 = Cipher.getInstance(encodeType);
            if(Cipher.ENCRYPT_MODE == cipherType){
                src = encryptMode(c1, index, src, encodeType);
            }
            //初始化
            if(spec != null) {
                if(secureRandom != null && iv != null){
                    c1.init(cipherType, spec, new IvParameterSpec(iv), secureRandom);
                }else if(secureRandom == null  && iv != null){
                    c1.init(cipherType, spec, new IvParameterSpec(iv));
                }else if(secureRandom != null  && iv == null){
                    c1.init(cipherType, spec, secureRandom);
                }else {
                    c1.init(cipherType, spec);
                }
            } else {
                if(secureRandom != null && iv != null) {
                    c1.init(cipherType, secretKey, new IvParameterSpec(iv), secureRandom);
                } else if(secureRandom == null  && iv != null) {
                    c1.init(cipherType, secretKey, new IvParameterSpec(iv));
                } else if(secureRandom != null  && iv == null) {
                    c1.init(cipherType, secretKey, secureRandom);
                } else {
                    c1.init(cipherType, secretKey);
                }
            }
            if(encodeType.toUpperCase().indexOf(SecretConstants.AES_KEY_ALGORITHM) < 0) {
                // 自行补位，达到8字节的倍数
                int remainder = src.length % 8;
                //  0 != remainder
                if (index > 0 && cipherType != 2) {
                    int oldLength = src.length;
                    // 1.扩展自身长度
                    src = Arrays.copyOf(src, src.length + 8 - remainder);
                    // 2.填充扩展内容为0
                    Arrays.fill(src, oldLength, src.length, (byte) 0);
                }
            }
            // 此处不能使用update,自行补位，
            //加解密
            return doFinal(c1, src);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionAndDecryptionException(e);
        } catch (InvalidKeyException e) {
            throw new EncryptionAndDecryptionException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new EncryptionAndDecryptionException(e);
        } catch (NoSuchPaddingException e) {
            throw new EncryptionAndDecryptionException(e);
        }
    }

    private static byte[] doFinal(Cipher c1, byte[] src) {
        try {
            return c1.doFinal(src);
        } catch (IllegalBlockSizeException e) {
            throw new EncryptionAndDecryptionException(e);
        } catch (BadPaddingException e) {
            throw new EncryptionAndDecryptionException(e);
        }
    }

    private static byte[] encryptMode(Cipher c1, int index, byte[] src, String encodeType) {
        if(index > 0){
            int blockSize = c1.getBlockSize();
            int length = src.length;
            if (length % blockSize != 0) {
                length = length + (blockSize - (length % blockSize));
            }
            int oldLength = src.length;
            // 1.扩展自身长度
            src = Arrays.copyOf(src, length);
            // 2.填充扩展内容为0
            Arrays.fill(src, oldLength, src.length, (byte) 0);
        } else if(encodeType.toUpperCase().indexOf(Padding.NOPADDING.toString()) > 0){
            int blockSize = c1.getBlockSize();
            int length = src.length;
            if (length % blockSize != 0) {
                length = length + (blockSize - (length % blockSize));
            }
            int oldLength = src.length;
            // 1.扩展自身长度
            src = Arrays.copyOf(src, length);
            // 2.填充扩展内容为0
            Arrays.fill(src, oldLength, src.length, (byte) ' ');
        }
        return src;
    }


    /**
     * 通过MessageDigest进行加解密运算
     * @param byteArray byte数组
     * @param key 密钥
     * @param encodeType 加密类型
     * @return 加解密数据
     */
    public static byte[] getMessageDigest(byte[] byteArray, byte[] key, String encodeType){
        MessageDigest messageDigest = null;
        byte[] digest = null;
        try {
            messageDigest = MessageDigest.getInstance(encodeType);
            if(key != null){
                messageDigest.update(key);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        if(byteArray != null){
            digest = messageDigest.digest(byteArray);
        }else {
            digest = messageDigest.digest();
        }
        return digest;
    }

    /**
     * 通过MessageDigest进行加解密运算
     * @param inStr 待加密解数据
     * @param keyStr  密钥key
     * @param encodeType 编码类型
     * @return 密文/明文
     */
    public static String getMessageDigest(String inStr,String keyStr, String encodeType){
        byte[] inStrByte = StringUtils.isBlank(inStr)? null:StringUtils.stringToBytes(inStr);
        byte[] keyStrByte = StringUtils.isBlank(keyStr)? null:StringUtils.stringToBytes(keyStr);
        byte[] messageDigest = getMessageDigest(inStrByte, keyStrByte, encodeType);
        return HexUtils.bytesToHexString(messageDigest);
    }

    /**
     * 使用KeyGenerator进行单向/双向加密（可设密码）
     * @param res 被加密的原文
     * @param algorithm  加密使用的算法名称
     * @param key 加密使用的秘钥
     * @return 密文/明文
     */
    public static String keyGeneratorMac(String res, String algorithm, String key){
        byte[] result = keyGeneratorMac(StringUtils.stringToBytes(res), algorithm, StringUtils.stringToBytes(key));
        return HexUtils.bytesToHexString(result);
    }
    /**
     * 使用KeyGenerator进行单向/双向加密（可设密码）
     * @param res 待加解密字节数组
     * @param algorithm  加密使用的算法名称
     * @param key key字节数组
     * @return 密文/明文
     */
    public static byte[] keyGeneratorMac(byte[] res, String algorithm, byte[] key){
        Mac mac = null;
        try {
            SecretKey sk = null;
            if (key == null) {
                KeyGenerator kg = KeyGenerator.getInstance(algorithm);
                sk = kg.generateKey();
            } else {
                sk = new SecretKeySpec(key, algorithm);
            }
            mac = Mac.getInstance(algorithm);
            mac.init(sk);
            return mac.doFinal(res);
        } catch (NoSuchAlgorithmException e) {
            TyLog.e(e.getMessage(), e);
            throw new IllegalArgumentException("算法：【" + algorithm + "】无法识别，");
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 非对称密匙工厂初始化
     * @param secretConsts 密钥
     * @return key
     */
    private static KeyFactory getKeyFactory(String secretConsts) {
        try {
            return KeyFactory.getInstance(secretConsts);
        } catch (NoSuchAlgorithmException e) {
            TyLog.e("算法：【" + secretConsts + "】无法识别，" + e.getMessage());
            return null;
        }
    }

    /**
     * 生产非对称密匙工厂
     * @param secretConsts 密钥
     * @param isPublicKey 是否公钥
     * @param keyByte 私钥
     * @return key
     */
    public static Key createKeyFactory(String secretConsts, boolean isPublicKey, byte[] keyByte){
        Key key = null;
        KeyFactory keyFactory = getKeyFactory(secretConsts);
        try {
            if(isPublicKey){
                // 初始化公钥
                X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyByte);
                // 产生公钥
                key = keyFactory.generatePublic(x509KeySpec);
            }else{
                //取得私钥
                PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByte);
                //产生私钥
                key = keyFactory.generatePrivate(pkcs8KeySpec);
            }
        } catch (InvalidKeySpecException e) {
            TyLog.e(e.getMessage(), e);
        } catch (NullPointerException e) {
            TyLog.e(e.getMessage(), e);
        }
        return key;
    }

    public static SecureRandom getSecureRandom(String providerType, String secureType, String secretKey){
        SecureRandom secureRandom = null;
        try {
            if(providerType != null){
                //防止linux下 随机生成key
                Provider p = Security.getProvider(providerType);
                secureRandom = SecureRandom.getInstance(secureType, p);
            }else {
                secureRandom = SecureRandom.getInstance(secureType);
            }
        } catch (NoSuchAlgorithmException e) {
            TyLog.e("算法：【" + providerType + "】无法识别，" + e.getMessage());
            throw new IllegalArgumentException(e);
        }

        secureRandom.setSeed(StringUtils.stringToBytes(secretKey));
        return secureRandom;
    }


    public static SecretKey getSecretKeyByKeyGenerator(String keyType,String key, int encryptLength, SecureRandom secureRandom){
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(keyType);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        if(secureRandom == null){
            if (key == null) {
                kg.init(encryptLength);
            } else{
                secureRandom = getSecureRandom(null, SecretConstants.SHA_ALGORITHM, key);
                //若想改为DES加密，则需要将秘钥位数改为64位 56 128 192 256 ENCRYPT_LENGTH
                kg.init(encryptLength, secureRandom);
            }
        }else {
            //若想改为DES加密，则需要将秘钥位数改为64位 56 128 192 256 ENCRYPT_LENGTH
            kg.init(encryptLength, secureRandom);
        }

        // 生成密钥
        return kg.generateKey();
    }

    /**
     * 对称密匙加密
     * @param key 密匙文本 des
     * @param keyType 加密方式
     * @return 密文
     */
    public static SecretKey getSecretKeyBySecretKeyFactory(String key, String keyType){
        SecretKey secretKey = null;
        DESedeKeySpec deSedeKeySpec = null;
        DESKeySpec desKeySpec = null;
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance(keyType);
            switch (keyType){
                case SecretConstants.DES3_KEY_ALGORITHM:
                    deSedeKeySpec = new DESedeKeySpec(StringUtils.stringToBytes(key));
                    secretKey = keyFactory.generateSecret(deSedeKeySpec);
                    break;
                case SecretConstants.DES_KEY_ALGORITHM:
                    desKeySpec = new DESKeySpec(StringUtils.stringToBytes(key));
                    secretKey = keyFactory.generateSecret(desKeySpec);
                    break;
                default:
                    break;
            }
        } catch (NoSuchAlgorithmException e) {
            TyLog.e("算法：【" + keyType + "】无法识别，" + e.getMessage());
        } catch (InvalidKeySpecException e) {
            TyLog.e("非法密匙：【" + deSedeKeySpec + "】无法识别，" + e.getMessage());
        } catch (InvalidKeyException e) {
            TyLog.e("密匙" + keyFactory + "生成失败，算法模式：" + keyType + e.getMessage());
        }
        return secretKey;
    }

    /**
     * 使用异或进行加密
     * @param res 需要加密的密文
     * @param key 秘钥
     * @return 密文
     */
    public String xoRencode(String res, String key) {
        byte[] bs = StringUtils.stringToBytes(key);
        for (int i = 0; i < bs.length; i++) {
            bs[i] = (byte) ((bs[i]) ^ key.hashCode());
        }
        return HexUtils.bytesToHexString(bs);
    }


    /**
     * 使用异或进行解密
     * @param res 需要解密的密文
     * @param key 秘钥
     * @return 明文
     */
    public String xoRdecode(String res, String key) {
        byte[] bs = HexUtils.hexStringToBytes(res);
        for (int i = 0; i < bs.length; i++) {
            bs[i] = (byte) ((bs[i]) ^ key.hashCode());
        }
        return StringUtils.bytesToString(bs);
    }

    /**
     * 直接使用异或（第一调用加密，第二次调用解密）
     * @param res 密文
     * @param key 秘钥
     * @return 加/解密
     */
    public int xor(int res, String key) {
        return res ^ key.hashCode();
    }
}
