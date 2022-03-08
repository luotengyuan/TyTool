package com.lois.tytool.base.secert;

/**
 * @Description 简单的异或加密
 * @Author Luo.T.Y
 * @Date 2022/2/23
 * @Time 14:39
 */
public class XorUtils {

    /**
     * 异或加密
     * @param str 待加密字符串
     * @param key 密钥
     * @return 加密字符串
     */
    public static String encode(String str, String key) {
        int[] snNum = new int[str.length()];
        String result = "";
        String temp = "";
        for (int i = 0, j = 0; i < str.length(); i++, j++) {
            if (j == key.length()) {
                j = 0;
            }
            snNum[i] = str.charAt(i) ^ key.charAt(j);
        }
        for (int k = 0; k < str.length(); k++) {
            if (snNum[k] < 10) {
                temp = "00" + snNum[k];
            } else {
                if (snNum[k] < 100) {
                    temp = "0" + snNum[k];
                }
            }
            result += temp;
        }
        return result;
    }

    /**
     * 异或解密
     * @param str 加密字符串
     * @param key 密钥
     * @return 解密后字符串
     */
    public static String decode(String str, String key) {
        char[] snNum = new char[str.length() / 3];
        String result = "";

        for (int i = 0, j = 0; i < str.length() / 3; i++, j++) {
            if (j == key.length()) {
                j = 0;
            }
            int n = Integer.parseInt(str.substring(i * 3, i * 3 + 3));
            snNum[i] = (char) ((char) n ^ key.charAt(j));
        }
        for (int k = 0; k < str.length() / 3; k++) {
            result += snNum[k];
        }
        return result;
    }
}
