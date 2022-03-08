package com.lois.tytool.base.secert.enumeration;
/**
 * RSA签名使用的算法类型
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public enum RsaSignAlgorithm {
    /**
     * 采用MD5withRSA算法签名
     */
    SIGNATURE_ALGORITHM_MD5,
    /**
     * 采用SHA1withRSA算法签名
     */
    SIGNATURE_ALGORITHM_SHA1,
    /**
     * 采用SHA256withRSA算法签名
     */
    SIGNATURE_ALGORITHM_SHA256,
    /**
     * 采用SHA384withRSA算法签名
     */
    SIGNATURE_ALGORITHM_SHA384,
    /**
     * 采用SHA512withRSA算法签名
     */
    SIGNATURE_ALGORITHM_SHA512;
}
