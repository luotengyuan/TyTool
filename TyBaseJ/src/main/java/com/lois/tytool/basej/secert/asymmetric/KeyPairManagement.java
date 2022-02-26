package com.lois.tytool.basej.secert.asymmetric;

import com.lois.tytool.basej.secert.Base64Utils;

/**
 * RSA算法密钥对管理
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class KeyPairManagement {
    /**
     * 公钥
     */
    private byte[] publicKey;
    /**
     * 私钥
     */
    private byte[] privateKey;

    public byte[] getPublicKey() {
        return publicKey.clone();
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey.clone();
    }

    public byte[] getPrivateKey() {
        return this.privateKey.clone();
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey.clone();
    }

    public String getPubliceKeyBase64() {
        return Base64Utils.encode(this.publicKey);
    }

    public String getPrivateKeyBase64() {
        return Base64Utils.encode(this.privateKey);
    }
}
