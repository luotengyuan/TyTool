package com.lois.tytool.base.secert.sm;

import com.lois.tytool.base.util.HexUtils;

import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

/**
 * SM2密钥对Bean
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class Sm2KeyPairUtils {

    private final ECPoint publicKey;
    private final BigInteger privateKey;

    public Sm2KeyPairUtils(ECPoint publicKey, BigInteger privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public ECPoint getPublicKey() {
        return publicKey;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public String getPublicKeyHex() {
        byte[] pubByte = publicKey.getEncoded(false);
        return HexUtils.bytesToHexString(pubByte);
    }

    public String getPrivateKeyHex() {
        byte[] priByte = HexUtils.bitIntegerToBytes(privateKey);
        return HexUtils.bytesToHexString(priByte);
    }

}

