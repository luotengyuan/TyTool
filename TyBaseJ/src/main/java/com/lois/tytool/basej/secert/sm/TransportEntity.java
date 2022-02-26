package com.lois.tytool.basej.secert.sm;

import org.bouncycastle.math.ec.ECPoint;

import java.io.Serializable;

/**
 * 传输实体类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class TransportEntity implements Serializable {
    /**
     * R点
     */
    final byte[] R;
    /**
     * 验证S
     */
    final byte[] S;
    /**
     * 用户标识
     */
    final byte[] Z;
    /**
     * 公钥
     */
    final byte[] K;

    public TransportEntity(byte[] r, byte[] s, byte[] z, ECPoint pKey) {
        if (r != null) {
            R = r.clone();
        } else {
            R = null;
        }
        if (s != null) {
            S = s.clone();
        } else {
            S = null;
        }
        if (z != null) {
            Z = z.clone();
        } else {
            Z = null;
        }
        if (pKey != null) {
            K = pKey.getEncoded(false);
        } else {
            K = null;
        }

    }
}
