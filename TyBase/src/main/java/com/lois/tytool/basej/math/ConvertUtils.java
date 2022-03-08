package com.lois.tytool.basej.math;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * @Description 转换工具
 * @Author Luo.T.Y
 * @Date 2017-09-22
 * @Time 10:47
 */
public class ConvertUtils {

    /**
     * char转byte
     *
     * @param chars
     * @return
     */
    public static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }

    /**
     * byte转char
     *
     * @param bytes
     * @return
     */
    public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }

    /**
     * 十六进制转字符串
     *
     * @param bytes
     * @param len
     * @return
     */
    public static String getHexStr(byte[] bytes, int len) {
        String strHex = "";
        if (len > bytes.length) {
            len = bytes.length;
        }
        for (int i = 0; i < len; i++) {
            String temp = "00" + Integer.toHexString(bytes[i]);
            strHex = strHex + temp.substring(temp.length() - 2, temp.length()) + " ";
        }
        return strHex;
    }

    /**
     * UTF-8 一个汉字占三个字节
     *
     * @param str 源字符串
     *            转换成字节数组的字符串
     * @return
     */
    public static byte[] stringToByte(String str, String charEncode) {
        byte[] destObj = null;
        try {
            if (null == str || str.trim().equals("")) {
                destObj = new byte[0];
                return destObj;
            } else {
                destObj = str.getBytes(charEncode);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return destObj;
    }

    /**
     * 字节数组转换成String
     *
     * @param srcObj 源字节数组转换成String的字节数组
     * @return
     */
    public static String byteToString(byte[] srcObj, String charEncode) {
        String destObj = null;
        try {
            destObj = new String(srcObj, charEncode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return destObj.replaceAll("\0", " ");
    }

    /**
     * byte Copy Of Length
     *
     * @param srcObj
     * @param len
     * @return
     */
    public static byte[] byteCopyOfLength(byte[] srcObj, int len) {
        byte[] destObj = null;
        destObj = new byte[len];
        if (srcObj.length < len) {
            System.arraycopy(srcObj, 0, destObj, 0, srcObj.length);
        } else {
            System.arraycopy(srcObj, 0, destObj, 0, len);
        }
        return destObj;
    }

    /**
     * string To BCD
     *
     * @param str1
     * @param dst
     * @param len
     * @return
     */
    public static boolean stringToBCD(String str1, byte[] dst, int len) {
        try {
            int s = 0;
            for (int m = 0; m < 2 * len; m++) {
                str1 = "0" + str1;
            }
            str1 = str1.substring(str1.length() - len * 2, str1.length());
            for (int i = 0; i < len; i++) {
                s = Integer.parseInt(str1.substring(2 * i, 2 * i + 2), 16);

                dst[i] = (byte) s;


            }
            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    /**
     * 将字节转化为整型
     *
     * @param a
     * @return Int 整数
     */
    public static int byteToInt(byte a) {
        return (a & 0xff);
    }

    /**
     * 将字节数组转成整数(大端模式,高字节在低位即byteArray[0])
     *
     * @param byteData
     * @return整数
     */
    public static int byteArrayToInt(byte[] byteData) {
        int result = 0;
        int len = 0;
        if (byteData == null || byteData.length == 0) {
            return 0;
        }
        len = byteData.length;
        for (int i = 0; i < len; i++) {
            result |= (byteData[i] & 0xff) << (len - 1 - i) * 8;
        }
        return result;
    }

    /**
     * 将数组中存的2个数组为整数
     *
     * @param a 最高位
     * @param b
     * @return Int 整数
     */
    public static int byteArraytoShort(byte a, byte b) {
        int result = 0;
        int a1 = byteToInt(a);
        int b1 = byteToInt(b);
        result += a1 * 256 + b1;

        if (a < 0) {
            result = result - 65536;
        }

        return result;
    }

    /**
     * 将数组中存的四个数组为整数
     *
     * @param a 最高位
     * @param b
     * @param c
     * @param d 最低位
     * @return Int 整数
     */
    public static int byteArraytoInt(byte a, byte b, byte c, byte d) {
        int result = 0;
        int a1 = byteToInt(a);
        int b1 = byteToInt(b);
        int c1 = byteToInt(c);
        int d1 = byteToInt(d);

        result += a1 * 256 * 256 * 256 + b1 * 256 * 256 + c1 * 256 + d1;
        return result;
    }

    /**
     * 整形转数组
     *
     * @param value
     * @return
     */
    public static byte[] intToByteArray(int value) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (byte) (value >> 8 * (3 - i) & 0xff);
        }
        return result;
    }

    /**
     * short型转数组
     *
     * @param value
     * @return
     */
    public static byte[] shortToByteArray(short value) {
        byte[] result = new byte[2];
        for (int i = 0; i < 2; i++) {
            result[i] = (byte) (value >> 8 * (1 - i) & 0xff);
        }
        return result;
    }

    /**
     * 将array list转化为数组
     *
     * @param arraylist
     * @return 数组
     */
    public static int[] getIntegerArrayByArrayList(ArrayList<Integer> arraylist) {
        if (arraylist != null) {
            int[] result = new int[arraylist.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = arraylist.get(i);
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * 将array list转化为数组
     *
     * @param arraylist
     * @return 数组
     */
    public static String[] getStringArrayByArrayList(ArrayList<String> arraylist) {
        if (arraylist != null) {
            String[] result = new String[arraylist.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = arraylist.get(i);
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * 将数组转化为arraylist
     *
     * @param src
     * @return 数组
     */
    public static ArrayList<Integer> getArrayListByIntegerArray(int[] src) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        if (src != null) {
            for (int i = 0; i < src.length; i++) {
                result.add(src[i]);
            }
        }
        return result;
    }

    /**
     * 将字符串转换为ArrayList
     *
     * @param src   待分隔字符串
     * @param split 分隔字符
     * @return
     */
    public static ArrayList<Integer> getArrayListByString(String src, String split) {
        ArrayList<Integer> idList = new ArrayList<Integer>();

        if (src != null && src.length() > 0) {
            String[] strs = src.split(split);
            for (int i = 0; i < strs.length; i++) {
                idList.add(stringToInt(strs[i]));
            }
        }

        return idList;
    }

    /**
     * 字符串转整形数
     *
     * @param str
     * @return 整形数, 如果字符串为空对象或字符串有效长度为0则返回0
     */
    public static int stringToInt(String str) {
        if (str == null) {
            return 0;
        }

        String tmpStr = str.trim();
        if (tmpStr.length() <= 0) {
            return 0;
        }
        return Integer.parseInt(tmpStr);
    }

    /**
     * 将二进制转化为16进制字符串
     *
     * @param b 二进制字节数组
     * @return String
     */
    public static String bytesToHexString(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if ((n + 1) % 4 == 0) {
                hs += " ";
            }
        }
        return hs.toUpperCase();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * asc转为bcd
     *
     * @param asc
     * @return
     */
    public static byte ascToBcd(byte asc) {
        byte bcd;
        if ((asc >= '0') && (asc <= '9')) {
            bcd = (byte) (asc - '0');
        } else if ((asc >= 'A') && (asc <= 'F')) {
            bcd = (byte) (asc - 'A' + 10);
        } else if ((asc >= 'a') && (asc <= 'f')) {
            bcd = (byte) (asc - 'a' + 10);
        } else {
            bcd = (byte) (asc - 48);
        }
        return bcd;
    }

    /**
     * asc转为bcd
     *
     * @param ascii
     * @param asc_len
     * @return
     */
    public static byte[] asciiToBcd(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = ascToBcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : ascToBcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }

    public static char[] hex = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
}
