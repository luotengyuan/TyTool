package com.lois.tytool.base.secert.enumeration;

/**
 * 填充模式枚举
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public enum Padding {
    /**
     * pkcs5padding填充模式
     */
    PKCS5PADDING,
    /**
     * PKCS7Padding填充模式
     */
    PKCS7PADDING,
    /**
     * nopadding填充模式
     */
    NOPADDING,
    /**
     * zeropadding填充模式
     */
    ZEROPADDING,
    /**
     * ISO10126PADDING填充模式
     */
    ISO10126
    /**
     * ANSIX923填充模式 暂时无法实现 TODO
     */
}
