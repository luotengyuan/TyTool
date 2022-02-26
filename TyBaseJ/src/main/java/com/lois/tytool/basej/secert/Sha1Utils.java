package com.lois.tytool.basej.secert;

import com.lois.tytool.basej.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description 安全散列算法1
 * 是一种密码散列函数，美国国家安全局设计，并由美国国家标准技术研究所（NIST）发布为联邦数据处理标准（FIPS）。SHA-1可以生成一个被称为消息摘要的160位（20字节）散列值，散列值通常的呈现形式为40个十六进制数。
 * @Author Luo.T.Y
 * @Date 2022/2/23
 * @Time 14:45
 */
public class Sha1Utils {

    /**
     * 获取字符串的sha1
     * @param str 字符串
     * @return sha1
     */
    public static String encode(String str) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String shaHex = Integer.toHexString(aMessageDigest & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取文件的sha1
     * @param file 文件
     * @return sha1
     */
    public static String encode(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            //10M memory
            byte[] b = new byte[1024 * 1024 * 10];
            int len;
            while ((len = in.read(b)) > 0) {
                messageDigest.update(b, 0, len);
            }
            return byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(in);
        }
        return null;
    }

    private static String byte2Hex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte aB : b) {
            String s = Integer.toHexString(aB & 0xFF);
            if (s.length() == 1) {
                sb.append("0");
            }
            sb.append(s);
        }
        return sb.toString();
    }
}
