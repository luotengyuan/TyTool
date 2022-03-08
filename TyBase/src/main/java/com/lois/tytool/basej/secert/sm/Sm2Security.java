package com.lois.tytool.basej.secert.sm;

import com.lois.tytool.basej.debug.TyLog;
import com.lois.tytool.basej.secert.Base64Utils;
import com.lois.tytool.basej.util.HexUtils;
import com.lois.tytool.basej.io.StreamUtils;
import com.lois.tytool.basej.string.StringUtils;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 国密SM2加解密
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class Sm2Security {

    private static BigInteger n = new BigInteger(
            "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "7203DF6B" + "21C6052B" + "53BBF409" + "39D54123", 16);
    private static BigInteger p = new BigInteger(
            "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFF", 16);
    private static BigInteger a = new BigInteger(
            "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFC", 16);
    private static BigInteger b = new BigInteger(
            "28E9FA9E" + "9D9F5E34" + "4D5A9E4B" + "CF6509A7" + "F39789F5" + "15AB8F92" + "DDBCBD41" + "4D940E93", 16);
    private static BigInteger gx = new BigInteger(
            "32C4AE2C" + "1F198119" + "5F990446" + "6A39C994" + "8FE30BBF" + "F2660BE1" + "715A4589" + "334C74C7", 16);
    private static BigInteger gy = new BigInteger(
            "BC3736A2" + "F4F6779C" + "59BDCEE3" + "6B692153" + "D0A9877C" + "C62A4740" + "02DF32E5" + "2139F0A0", 16);

    private static int w = (int) Math.ceil(n.bitLength() * 1.0 / 2) - 1;
    private static BigInteger TWO_W = new BigInteger("2").pow(w);
    private static final int DIGEST_LENGTH = 32;

    private static SecureRandom random = new SecureRandom();
    private static ECCurve.Fp curve = new ECCurve.Fp(p, a, b, null, null);
    private static ECPoint G = curve.createPoint(gx, gy);
    private static ECDomainParameters ecc_bc_spec = new ECDomainParameters(curve, G, n);

    private boolean debug = false;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * 以16进制打印字节数组
     *
     * @param b 需要转换打印的字节数组
     * @return 转换后的字符串
     */
    public static String printHexString(byte[] b) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            builder.append(hex.toUpperCase());
        }
        TyLog.v("16进制打印数组:" + builder.toString());
        return builder.toString();
    }

    /**
     * 随机数生成器
     *
     * @param max 最大值
     * @return 随机数
     */
    private static BigInteger random(BigInteger max) {
        BigInteger r = new BigInteger(256, random);
        while (r.compareTo(max) >= 0) {
            r = new BigInteger(128, random);
        }
        return r;
    }

    /**
     * 判断字节数组是否全0
     *
     * @param buffer 字节参数
     * @return 是否为0
     */
    private boolean allZero(byte[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 公钥加密
     * @param input 加密原文
     * @param publicKey 公钥
     * @return 密文字节
     */
    public byte[] encrypt(String input, ECPoint publicKey) {

        byte[] inputBuffer = StringUtils.stringToBytes(input);
        if (debug) {
            printHexString(inputBuffer);
        }
        byte[] encryByte =  encrypt(inputBuffer, publicKey);
        return encryByte;
    }
    /**
     * 公钥加密
     * @param input 加密原文
     * @param publicKey 公钥
     * @return base64密文
     */
    public String encryptStr(String input, ECPoint publicKey) {
        byte[] encryByte =  encrypt(input, publicKey);
        return Base64Utils.encode(encryByte);
    }


    /**
     * 公钥加密
     * @param input 加密原文
     * @param publicKeyHex 16进制公钥字符串
     * @return 公钥加密的密文
     */
    public byte[] encrypt(String input, String publicKeyHex) {

        byte[] inputBuffer = StringUtils.stringToBytes(input);
        if (debug) {
            printHexString(inputBuffer);
        }
        byte[] pubByte = HexUtils.hexStringToBytes(publicKeyHex);
        ECPoint pubPoint = curve.decodePoint(pubByte);
        byte[] encryByte = encrypt(inputBuffer, pubPoint);
        return encryByte;
    }

    public String encryptStr(String input, String publicKeyHex) {
        byte[] encryByte = encrypt(input, publicKeyHex);
        return Base64Utils.encode(encryByte);
    }

    /**
     * 公钥加密
     *
     * @param inputBuffer 加密原文
     * @param publicKey 公钥
     * @return 密文
     */
    public byte[] encrypt(byte[] inputBuffer, ECPoint publicKey) {

        byte[] c1Buffer;
        ECPoint kpb;
        byte[] t;
        do {
            /* 1 产生随机数k，k属于[1, n-1] */
            BigInteger k = random(n);
            if (debug) {
                TyLog.v("k: ");
                printHexString(k.toByteArray());
            }

            /* 2 计算椭圆曲线点C1 = [k]G = (x1, y1) */
            ECPoint c1 = G.multiply(k);
            c1Buffer = c1.getEncoded(false);
            if (debug) {
                TyLog.v("c1: ");
                printHexString(c1Buffer);
            }

            // 计算椭圆曲线点 S = [h]Pb
            BigInteger h = ecc_bc_spec.getH();
            if (h != null) {
                ECPoint s = publicKey.multiply(h);
                if (s.isInfinity()) {
                    throw new IllegalStateException();
                }
            }

            //4 计算 [k]PB = (x2, y2)
            kpb = publicKey.multiply(k).normalize();

            //5 计算 t = KDF(x2||y2, klen)
            byte[] kpbBytes = kpb.getEncoded(false);
            t = kdf(kpbBytes, inputBuffer.length);
        } while (allZero(t));

        //6 计算C2=M^t
        byte[] c2 = new byte[inputBuffer.length];
        for (int i = 0; i < inputBuffer.length; i++) {
            c2[i] = (byte) (inputBuffer[i] ^ t[i]);
        }

        //7 计算C3 = Hash(x2 || M || y2)
        byte[] c3 = sm3hash(kpb.getXCoord().toBigInteger().toByteArray(), inputBuffer,
                kpb.getYCoord().toBigInteger().toByteArray());

        //8 输出密文 C=C1 || c2 || c3
        byte[] encryptResult = new byte[c1Buffer.length + c2.length + c3.length];

        System.arraycopy(c1Buffer, 0, encryptResult, 0, c1Buffer.length);
        System.arraycopy(c2, 0, encryptResult, c1Buffer.length, c2.length);
        System.arraycopy(c3, 0, encryptResult, c1Buffer.length + c2.length, c3.length);

        if (debug) {
            TyLog.v("密文：");
            printHexString(encryptResult);
        }

        return encryptResult;
    }

    /**
     * 公钥加密
     *
     * @param inputfile 加密原文
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 文件找不到
     */
    public byte[] encrypt(File inputfile, ECPoint publicKey) throws Exception {
        if(inputfile.exists()) {
            InputStream inStream = new FileInputStream(inputfile);
            byte[] inputBuffer = StreamUtils.readInputStreamToByteArray(inStream);
            return encrypt(inputBuffer, publicKey);
        } else {
            throw new FileNotFoundException();
        }
    }

    /**
     * 私钥解密
     * @param encryptData 密文数据字节数组
     * @param privateKeyHex 16进制字符串私钥
     * @return 明文
     */
    public String decrypt(byte[] encryptData, String privateKeyHex) {
        byte[] priByte = HexUtils.hexStringToBytes(privateKeyHex);
        BigInteger bigInteger = new BigInteger(1, priByte);
        return decrypt(encryptData, bigInteger);
    }

    /**
     * 私钥解密
     * @param encryptBase64Data base64格式密文数据
     * @param privateKeyHex 16进制字符串私钥
     * @return 明文
     */
    public String decryptStr(String encryptBase64Data, String privateKeyHex) {
        byte[] priByte = HexUtils.hexStringToBytes(privateKeyHex);
        BigInteger bigInteger = new BigInteger(1, priByte);
        byte[] decryBase64 = Base64Utils.decode(encryptBase64Data);
        return decrypt(decryBase64, bigInteger);
    }

    /**
     * 私钥解密
     * @param encryptBase64Data base64格式密文数据
     * @param privateKey 解密私钥
     * @return 明文
     */
    public String decryptStr(String encryptBase64Data, BigInteger privateKey) {
        byte[] decryBase64 = Base64Utils.decode(encryptBase64Data);
        return decrypt(decryBase64, privateKey);
    }
    /**
     * 私钥解密
     *
     * @param encryptData 密文数据字节数组
     * @param privateKey 解密私钥
     * @return 明文
     */
    public String decrypt(byte[] encryptData, BigInteger privateKey) {

        if (debug) {
            TyLog.v("encryptData length:{}", encryptData.length);
        }

        byte[] c1Byte = new byte[65];
        System.arraycopy(encryptData, 0, c1Byte, 0, c1Byte.length);

        ECPoint c1 = curve.decodePoint(c1Byte).normalize();

        /*
         * 计算椭圆曲线点 S = [h]c1 是否为无穷点
         */
        BigInteger h = ecc_bc_spec.getH();
        if (h != null) {
            ECPoint s = c1.multiply(h);
            if (s.isInfinity()) {
                throw new IllegalStateException();
            }
        }
        /* 计算[dB]c1 = (x2, y2) */
        ECPoint dBc1 = c1.multiply(privateKey).normalize();

        /* 计算t = KDF(x2 || y2, klen) */
        byte[] dBc1Bytes = dBc1.getEncoded(false);
        int klen = encryptData.length - 65 - DIGEST_LENGTH;
        byte[] t = kdf(dBc1Bytes, klen);

        if (allZero(t)) {
            TyLog.v("all zero");
            throw new IllegalStateException();
        }

        /* 5 计算M'=C2^t */
        byte[] m = new byte[klen];
        for (int i = 0; i < m.length; i++) {
            m[i] = (byte) (encryptData[c1Byte.length + i] ^ t[i]);
        }
        if (debug) {
            printHexString(m);
        }

        /* 6 计算 u = Hash(x2 || m' || y2) 判断 u == C3是否成立 */
        byte[] c3 = new byte[DIGEST_LENGTH];

        if (debug) {
            try {
                TyLog.v("m = " + new String(m, "UTF8"));
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        System.arraycopy(encryptData, encryptData.length - DIGEST_LENGTH, c3, 0, DIGEST_LENGTH);
        byte[] u = sm3hash(dBc1.getXCoord().toBigInteger().toByteArray(), m,
                dBc1.getYCoord().toBigInteger().toByteArray());
        if (Arrays.equals(u, c3)) {
            if (debug) {
                TyLog.v("解密成功");
            }
            try {
                return new String(m, "UTF8");
            } catch (UnsupportedEncodingException e) {
                TyLog.e(e.getMessage(), e);
            }
            return null;
        } else {
            if (debug) {
                TyLog.v("u = ");
                printHexString(u);
                TyLog.v("c3 = ");
                printHexString(c3);
                System.err.println("解密验证失败");
            }
            return null;
        }

    }

    /**
     * 判断是否在范围内
     *
     * @param param 参数
     * @param min 最小值
     * @param max 最大值
     * @return 是否在范围内
     */
    private boolean between(BigInteger param, BigInteger min, BigInteger max) {
        if (param.compareTo(min) >= 0 && param.compareTo(max) < 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断生成的公钥是否合法
     *
     * @param publicKey 公钥
     * @return 是否合法的公钥
     */
    private boolean checkPublicKey(ECPoint publicKey) {

        if (!publicKey.isInfinity()) {

            BigInteger x = publicKey.getXCoord().toBigInteger();
            BigInteger y = publicKey.getYCoord().toBigInteger();

            if (between(x, new BigInteger("0"), p) && between(y, new BigInteger("0"), p)) {

                BigInteger xResult = x.pow(3).add(a.multiply(x)).add(b).mod(p);

                if (debug) {
                    TyLog.v("xResult: " + xResult.toString());
                }

                BigInteger yResult = y.pow(2).mod(p);

                if (debug) {
                    TyLog.v("yResult: " + yResult.toString());
                }

                if (yResult.equals(xResult) && publicKey.multiply(n).isInfinity()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 生成密钥对
     *
     * @return 密钥对
     */
    public Sm2KeyPairUtils generateKeyPair() {

        BigInteger d = random(n.subtract(new BigInteger("1")));

        Sm2KeyPairUtils keyPair = new Sm2KeyPairUtils(G.multiply(d).normalize(), d);

        if (checkPublicKey(keyPair.getPublicKey())) {
            if (debug) {
                TyLog.v("generate key successfully");
            }
            return keyPair;
        } else {
            if (debug) {
                System.err.println("generate key failed");
            }
            return null;
        }
    }

    public Sm2Security() {
    }

    public Sm2Security(boolean debug) {
        this();
        this.debug = debug;
    }

    /**
     * 导出公钥到本地
     *
     * @param publicKey 公钥
     * @param path 导入的路径
     */
    public void exportPublicKey(ECPoint publicKey, String path) {
        File file = new File(path);
        FileOutputStream fos = null;

        try {
            if (!file.exists()) {
                boolean result = file.createNewFile();
                if (!result) {
                    throw new IllegalArgumentException("文件【" + path + "】创建失败。");
                }
            }
            byte[] buffer = publicKey.getEncoded(false);
            fos = new FileOutputStream(file);
            fos.write(buffer);
        } catch (IOException e) {
            TyLog.e(e.getMessage(), e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                TyLog.e(e.getMessage(), e);
            }
        }
    }

    /**
     * 从本地导入公钥
     *
     * @param path 公钥路径
     * @return 公钥
     */
    public ECPoint importPublicKey(String path) {
        File file = new File(path);
        FileInputStream fis = null;
        try {
            if (!file.exists()) {
                return null;
            }
            fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[16];
            int size;
            while ((size = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, size);
            }

            return curve.decodePoint(baos.toByteArray());
        } catch (IOException e) {
            TyLog.e(e.getMessage(), e);
        } finally {

            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                TyLog.e(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 导出私钥到本地
     *
     * @param privateKey 私钥
     * @param path 导出的路径
     */
    public void exportPrivateKey(BigInteger privateKey, String path) {
        File file = new File(path);
        ObjectOutputStream oos = null;
        try {
            if (!file.exists()) {
                boolean result = file.createNewFile();
                if (!result) {
                    throw new IllegalArgumentException("文件【" + path + "】创建失败。");
                }
            }
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(privateKey);

        } catch (IOException e) {
            TyLog.e(e.getMessage(), e);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                TyLog.e(e.getMessage(), e);
            }
        }
    }

    /**
     * 从本地导入私钥
     *
     * @param path 私钥路径
     * @return 私钥
     */
    public BigInteger importPrivateKey(String path) {
        File file = new File(path);
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            if (!file.exists()) {
                return null;
            }
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            BigInteger res = (BigInteger) (ois.readObject());

            return res;
        } catch (Exception e) {
            TyLog.e(e.getMessage(), e);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                TyLog.e(e.getMessage(), e);
            }

        }
        return null;
    }

    /**
     * 字节数组拼接
     *
     * @param params 字节数组
     * @return 拼接后的字节数组
     */
    private static byte[] join(byte[]... params) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] res = null;
        try {
            for (int i = 0; i < params.length; i++) {
                baos.write(params[i]);
            }
            res = baos.toByteArray();
        } catch (IOException e) {
            TyLog.e(e.getMessage(), e);
        }
        return res;
    }

    /**
     * sm3摘要
     *
     * @param params 参数
     * @return 摘要
     */
    private static byte[] sm3hash(byte[]... params) {
        byte[] res = null;
        try {
            res = Sm3Security.hash(join(params));
        } catch (IOException e) {
            TyLog.e(e.getMessage(), e);
        }
        return res;
    }

    /**
     * 取得用户标识字节数组
     *
     * @param ida 签名方唯一标识
     * @param aPublicKey 签名方公钥
     * @return 用户标识字节数组
     */
    private static byte[] za(String ida, ECPoint aPublicKey) {
        byte[] idaBytes = StringUtils.stringToBytes(ida);
        int entlenA = idaBytes.length * 8;
        byte[] entla = new byte[] { (byte) (entlenA & 0xFF00), (byte) (entlenA & 0x00FF) };
        byte[] za = sm3hash(entla, idaBytes, a.toByteArray(), b.toByteArray(), gx.toByteArray(), gy.toByteArray(),
                aPublicKey.getXCoord().toBigInteger().toByteArray(),
                aPublicKey.getYCoord().toBigInteger().toByteArray());
        return za;
    }

    /**
     * 签名
     *
     * @param s1 签名信息
     * @param ida 签名方唯一标识
     * @param keyPair 签名方密钥对
     * @return 签名
     */
    public Signature sign(String s1, String ida, Sm2KeyPairUtils keyPair) {
        byte[] za = za(ida, keyPair.getPublicKey());
        byte[] m = join(za, StringUtils.stringToBytes(s1));
        BigInteger e = new BigInteger(1, sm3hash(m));

        BigInteger k;
        BigInteger r;
        do {
            k = random(n);
            ECPoint p1 = G.multiply(k).normalize();
            BigInteger x1 = p1.getXCoord().toBigInteger();
            r = e.add(x1);
            r = r.mod(n);
        } while (r.equals(BigInteger.ZERO) || r.add(k).equals(n));

        BigInteger s = ((keyPair.getPrivateKey().add(BigInteger.ONE).modInverse(n))
                .multiply((k.subtract(r.multiply(keyPair.getPrivateKey()))).mod(n))).mod(n);

        return new Signature(r, s);
    }

    /**
     * 签名
     * @param s1 签名信息
     * @param ida 签名方唯一标识
     * @param publicKeyHex 16进制公钥字符串
     * @param privateKeyHex 16进制私钥字符串
     * @return 签名
     */
    public Signature sign(String s1, String ida, String publicKeyHex, String privateKeyHex) {
        byte[] pubByte = HexUtils.hexStringToBytes(publicKeyHex);
        byte[] priByte = HexUtils.hexStringToBytes(privateKeyHex);

        ECPoint pubPoint = curve.decodePoint(pubByte);
        BigInteger privateKey = HexUtils.bytesToInteger(priByte);
        Sm2KeyPairUtils keyPairUtils = new Sm2KeyPairUtils(pubPoint, privateKey);
        return sign(s1, ida, keyPairUtils);
    }


    /**
     * 验签
     *
     * @param s 签名信息
     * @param signature 签名
     * @param ida 签名方唯一标识
     * @param aPublicKey 签名方公钥
     * @return true or false
     */
    public boolean verify(String s, Signature signature, String ida, ECPoint aPublicKey) {
        if (!between(signature.r, BigInteger.ONE, n)) {
            return false;
        }
        if (!between(signature.s, BigInteger.ONE, n)) {
            return false;
        }

        byte[] m = join(za(ida, aPublicKey), StringUtils.stringToBytes(s));
        BigInteger e = new BigInteger(1, sm3hash(m));
        BigInteger t = signature.r.add(signature.s).mod(n);

        if (t.equals(BigInteger.ZERO)) {
            return false;
        }

        ECPoint p1 = G.multiply(signature.s).normalize();
        ECPoint p2 = aPublicKey.multiply(t).normalize();
        BigInteger x1 = p1.add(p2).normalize().getXCoord().toBigInteger();
        BigInteger r = e.add(x1).mod(n);
        if (r.equals(signature.r)) {
            return true;
        }
        return false;
    }

    public boolean verify(String s, Signature signature, String ida, String publicKeyHex) {
        byte[] pubByte = HexUtils.hexStringToBytes(publicKeyHex);
        ECPoint pubPoint = curve.decodePoint(pubByte);
        return verify(s, signature, ida, pubPoint);
    }

    /**
     * 密钥派生函数
     *
     * @param z
     * @param klen 生成klen字节数长度的密钥
     * @return 密钥派
     */
    private static byte[] kdf(byte[] z, int klen) {
        int ct = 1;
        int end = (int) Math.ceil(klen * 1.0 / 32);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            for (int i = 1; i < end; i++) {
                baos.write(sm3hash(z, Sm3Security.toByteArray(ct)));
                ct++;
            }
            byte[] last = sm3hash(z, Sm3Security.toByteArray(ct));
            if (klen % 32 == 0) {
                baos.write(last);
            } else {
                baos.write(last, 0, klen % 32);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            TyLog.e(e.getMessage(), e);
            
        }
        return null;
    }

    /**
     * 密钥协商辅助类
     */
    public static class KeyExchange {
        BigInteger rA;
        ECPoint ra;
        ECPoint v;
        byte[] z;
//        byte[] key;
        Sm2KeyPairUtils keyPair;
        public KeyExchange(String id, Sm2KeyPairUtils keyPair) {
            this.keyPair = keyPair;
            this.z = za(id, keyPair.getPublicKey());
        }

        /**
         * 密钥协商发起第一步
         *
         * @return TransportEntity封装对象
         */
        public TransportEntity keyExchange1() {
            rA = random(n);
            ra = G.multiply(rA).normalize();
            return new TransportEntity(ra.getEncoded(false), null, z, keyPair.getPublicKey());
        }

        /**
         * 密钥协商响应方
         *
         * @param entity 传输实体
         * @return TransportEntity对象
         */
        public TransportEntity keyExchange2(TransportEntity entity) {
            BigInteger rB = random(n);
            // BigInteger rB=new BigInteger("33FE2194 0342161C 55619C4A 0C060293
            // D543C80A F19748CE 176D8347 7DE71C80".replace(" ", ""),16);
            ECPoint rb = G.multiply(rB).normalize();

            this.rA=rB;
            this.ra =rb;

            BigInteger x2 = rb.getXCoord().toBigInteger();
            x2 = TWO_W.add(x2.and(TWO_W.subtract(BigInteger.ONE)));

            BigInteger tB = keyPair.getPrivateKey().add(x2.multiply(rB)).mod(n);
            ECPoint ra = curve.decodePoint(entity.R).normalize();

            BigInteger x1 = ra.getXCoord().toBigInteger();
            x1 = TWO_W.add(x1.and(TWO_W.subtract(BigInteger.ONE)));

            ECPoint aPublicKey=curve.decodePoint(entity.K).normalize();
            ECPoint temp = aPublicKey.add(ra.multiply(x1).normalize()).normalize();
            ECPoint v = temp.multiply(ecc_bc_spec.getH().multiply(tB)).normalize();
            if (v.isInfinity()) {
                throw new IllegalStateException();
            }
            this.v =v;

            byte[] xV = v.getXCoord().toBigInteger().toByteArray();
            byte[] yV = v.getYCoord().toBigInteger().toByteArray();
            byte[] kb = kdf(join(xV, yV, entity.Z, this.z), 16);
            TyLog.v("协商得B密钥:");
            printHexString(kb);
            byte[] sB = sm3hash(new byte[] { 0x02 }, yV,
                    sm3hash(xV, entity.Z, this.z, ra.getXCoord().toBigInteger().toByteArray(),
                            ra.getYCoord().toBigInteger().toByteArray(), rb.getXCoord().toBigInteger().toByteArray(),
                            rb.getYCoord().toBigInteger().toByteArray()));
            return new TransportEntity(rb.getEncoded(false), sB,this.z,keyPair.getPublicKey());
        }

        /**
         * 密钥协商发起方第二步
         *
         * @param entity 传输实体
         * @return 传输实体封装
         */
        public TransportEntity keyExchange3(TransportEntity entity) {
            BigInteger x1 = ra.getXCoord().toBigInteger();
            x1 = TWO_W.add(x1.and(TWO_W.subtract(BigInteger.ONE)));

            BigInteger tA = keyPair.getPrivateKey().add(x1.multiply(rA)).mod(n);
            ECPoint rb = curve.decodePoint(entity.R).normalize();

            BigInteger x2 = rb.getXCoord().toBigInteger();
            x2 = TWO_W.add(x2.and(TWO_W.subtract(BigInteger.ONE)));

            ECPoint bPublicKey=curve.decodePoint(entity.K).normalize();
            ECPoint temp = bPublicKey.add(rb.multiply(x2).normalize()).normalize();
            ECPoint u = temp.multiply(ecc_bc_spec.getH().multiply(tA)).normalize();
            if (u.isInfinity()) {
                throw new IllegalStateException();
            }
            this.v =u;

            byte[] xU = u.getXCoord().toBigInteger().toByteArray();
            byte[] yU = u.getYCoord().toBigInteger().toByteArray();
            byte[] ka = kdf(join(xU, yU,
                    this.z, entity.Z), 16);
//            key = ka;
            TyLog.v("协商得A密钥:");
            printHexString(ka);
            byte[] s1= sm3hash(new byte[] { 0x02 }, yU,
                    sm3hash(xU, this.z, entity.Z, ra.getXCoord().toBigInteger().toByteArray(),
                            ra.getYCoord().toBigInteger().toByteArray(), rb.getXCoord().toBigInteger().toByteArray(),
                            rb.getYCoord().toBigInteger().toByteArray()));
            if(Arrays.equals(entity.S, s1)) {
                TyLog.v("B->A 密钥确认成功");
            } else {
                TyLog.v("B->A 密钥确认失败");
            }
            byte[] sA= sm3hash(new byte[] { 0x03 }, yU,
                    sm3hash(xU, this.z, entity.Z, ra.getXCoord().toBigInteger().toByteArray(),
                            ra.getYCoord().toBigInteger().toByteArray(), rb.getXCoord().toBigInteger().toByteArray(),
                            rb.getYCoord().toBigInteger().toByteArray()));

            return new TransportEntity(ra.getEncoded(false), sA,this.z,keyPair.getPublicKey());
        }

        /**
         * 密钥确认最后一步
         *
         * @param entity 传输实体
         */
        public void keyExchange4(TransportEntity entity) {
            byte[] xV = v.getXCoord().toBigInteger().toByteArray();
            byte[] yV = v.getYCoord().toBigInteger().toByteArray();
            ECPoint ra = curve.decodePoint(entity.R).normalize();
            byte[] s2= sm3hash(new byte[] { 0x03 }, yV,
                    sm3hash(xV, entity.Z, this.z, ra.getXCoord().toBigInteger().toByteArray(),
                            ra.getYCoord().toBigInteger().toByteArray(), this.ra.getXCoord().toBigInteger().toByteArray(),
                            this.ra.getYCoord().toBigInteger().toByteArray()));
            if(Arrays.equals(entity.S, s2)) {
                TyLog.v("A->B 密钥确认成功");
            } else {
                TyLog.v("A->B 密钥确认失败");
            }
        }
    }



    public static class Signature {
        BigInteger r;
        BigInteger s;

        public Signature(BigInteger r, BigInteger s) {
            this.r = r;
            this.s = s;
        }

        @Override
        public String toString() {
            return r.toString(16) + "," + s.toString(16);
        }
    }
}
