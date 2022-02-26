package com.lois.tytool.basej.secert.enumeration;
/**
 * hmachash散列算法类型
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public enum HmacHashType {
    /**
     * 含有密钥散列函数算法：HMAC_SHA
     */
    HMAC_SHA,

    /**
     * 含有密钥散列函数算法：HMAC_SHA256
     */
    HMAC_SHA256,

    /**
     * 含有密钥散列函数算法：HMAC_SHA512
     */
    HMAC_SHA512;
}
