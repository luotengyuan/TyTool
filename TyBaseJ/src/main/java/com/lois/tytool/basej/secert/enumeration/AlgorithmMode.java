package com.lois.tytool.basej.secert.enumeration;

/**
 * 算法模式枚举
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public enum AlgorithmMode {
    /**
     * 电码本模式（Electronic Codebook Book）
     * 将整个明文分成若干段相同的小段，然后对每一小段进行加密
     */
    ECB,
    /**
     * 密码分组链接模式（Cipher Block Chaining）
     * 先将明文切分成若干小段，然后每一小段与初始块或者上一段的密文段进行异或运算后，再与密钥进行加密
     */
    CBC,
    /**
     * 计算器模式（Counter）
     * 有一个自增的算子，这个算子用密钥加密之后的输出和明文异或的结果得到密文，相当于一次一密。
     * 这种加密方式简单快速，安全可靠，而且可以并行加密，但是 在计算器不能维持很长的情况下，密钥只能使用一次
     */
    CTR,
    /**
     * 输出反馈模式（Output FeedBack）
     */
    OFB,
    /**
     * 密码反馈模式（Cipher FeedBack）
     */
    CFB
}
