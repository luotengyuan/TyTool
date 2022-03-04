package com.lois.tytool.basej.string;

import com.lois.tytool.basej.constant.FileConstants;
import com.lois.tytool.basej.math.ConvertUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 字符串相关工具类
 * @Author Luo.T.Y
 * @Date 2017-09-22
 * @Time 14:15
 */
public class StringUtils {

    /**
     * 判断两个 CharSequence 是否相等
     *
     * @param s1 CharSequence 1
     * @param s2 CharSequence 2
     * @return true 表示相等
     */
    public static boolean equals(final CharSequence s1, final CharSequence s2) {
        if (s1 == s2) {
            return true;
        }
        int length;
        if (s1 != null && s2 != null && (length = s1.length()) == s2.length()) {
            if (s1 instanceof String && s2 instanceof String) {
                return s1.equals(s2);
            } else {
                for (int i = 0; i < length; i++) {
                    if (s1.charAt(i) != s2.charAt(i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 忽略大小写之后，判断两个 String 是否相等
     *
     * @param s1 String 1
     * @param s2 String 2
     * @return true 表示相等
     */
    public static boolean equalsIgnoreCase(final String s1, final String s2) {
        return s1 == null ? s2 == null : s1.equalsIgnoreCase(s2);
    }

    /**
     * 判断字符串是否为纯数字
     *
     * @param str 字符串
     * @return 是否纯数字
     */
    public static boolean isNumber(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否全为数字并且几位以上
     *
     * @param str 字符创
     * @param num 包含数字至少几位
     * @return true是，false否
     */
    public static boolean isNumber(String str, int num) {
        String[] number = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        int sameNum = 0;
        int j, i;

        if (str == null || str.length() == 0) {
            return false;
        }
        for (i = 0; i < str.length(); i++) {
            for (j = 0; j < number.length; j++) {
                if (number[j].equals(str.substring(i, i + 1))) {
                    sameNum++;
                    break;
                }
            }
            if (j >= number.length) {
                return false;
            }
        }
        if (sameNum < num) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 将字符串中的0去除
     *
     * @param str
     * @return string
     */
    public static String getStringNoZero(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '\0') {
                sb.append(str.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * 判断该字符是否是数字
     *
     * @param c 字符
     * @return true 是 false否
     */
    public static boolean isCharNumber(char c) {
        if (c >= '0' && c <= '9') {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查找指定字符位置
     *
     * @param data     : 待查找数据
     * @param findchar :待查找字符
     * @param numchar  ：待查找字符的序号,从0开始
     * @return返回指定字符出现的位置,如果没有找到,返回数据总长度
     */
    public static int findCharPos(byte[] data, char findchar, int numchar) {
        if (data == null || data.length == 0) {
            return -1;
        }
        int size = data.length;
        int pos = 0;
        for (; ; ) {
            if (data[pos] == findchar) {
                if (numchar == 0) {
                    break;
                } else {
                    numchar--;
                }
            }
            size--;
            pos++;
            if (size == 0) {
                break;
            }
        }
        return pos;
    }

    /**
     * 查找指定字符位置
     *
     * @param str      待查找字符串
     * @param findchar 待查找字符
     * @param numchar  待查找字符的序号, 从0开始, 如: numchar=1, 则表示查找字符第2次出现的位置
     * @param maxlen   待查找字符串的最大长度
     * @return 返回指定字符出现的位置, 从0开始, 如果没有找到, 返回字符串的长度
     */
    public static int findCharPos(String str, char findchar, int numchar, int maxlen) {
        int i, pos;
        if (str == null) {
            return 0;
        }
        pos = 0;
        maxlen = (str.length() > maxlen) ? maxlen : str.length();
        for (i = 0; i < maxlen; i++) {
            if (str.charAt(i) == findchar) {
                if (numchar == 0) {
                    break;
                } else {
                    numchar--;
                }
            }
            pos++;
        }
        return pos;
    }

    /**
     * 查找指定字符数量
     *
     * @param data     : 待查找数据
     * @param findchar :待查找字符
     * @return返回指定字符数量
     */
    public static int findCharNum(byte[] data, char findchar) {
        if (data == null || data.length == 0) {
            return 0;
        }
        int size = data.length;
        int pos = 0;
        int numChar = 0;
        for (; ; ) {
            if (data[pos] == findchar) {
                numChar++;
            }
            size--;
            pos++;
            if (size == 0) {
                break;
            }
        }
        return numChar;
    }

    /**
     * 查找指定字符的数量
     *
     * @param str      待查找的字符串
     * @param findchar 待查找字符
     * @param maxlen   待查找字符串的最大长度
     * @return 指定字符的数量
     */
    public static int findCharNum(String str, char findchar, int maxlen) {
        int i, numchar = 0;
        if (str == null) {
            return 0;
        }
        maxlen = (str.length() > maxlen) ? maxlen : str.length();
        for (i = 0; i < maxlen; i++) {
            if (str.charAt(i) == findchar) {
                numchar++;
            }
        }

        return numchar;
    }

    /**
     * 将字符串拼成六位+加单位为七位 后面加空格
     *
     * @param str
     * @return拼接后的字符串
     */
    public static String getSixBytesStr(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() >= 7) {
            return str;
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append(str);
            for (int i = str.length(); i < 7; i++) {
                sb.append(" ");
            }
            return sb.toString();
        }
    }

    /**
     * 根据分隔符拆分输入字符串为字符串数组, 允许两分隔符间无内容
     *
     * @param src   待分隔字符串
     * @param split 分隔字符
     * @return 分隔后数组, 空数据返回空串
     */
    public static String[] splitToArray(String src, String split) {
        String[] strs;
        if (src == null || src.length() == 0) {
            return null;
        }
        strs = src.split(split);
        return strs;
    }

    /**
     * 根据分隔符拆分输入字符串为整型数组, 允许两分隔符间无内容
     *
     * @param src   待分隔字符串
     * @param split 分隔字符
     * @return 分隔后数组, 空数据返回空串
     */
    public static int[] splitToIntArray(String src, char split) {
        int i, len, count, start, end;
        int[] pos;
        char[] chars;
        int[] strs;

        if (src == null || src.length() == 0) {
            return null;
        }

        chars = src.toCharArray();
        len = chars.length;
        count = 0;
        pos = new int[len];
        for (i = 0; i < len; i++) {
            // 获取每个分隔符的位置
            if (chars[i] == split) {
                pos[count++] = i;
            }
        }
        // 子串的个数等于分隔符的个数加1
        strs = new int[count + 1];
        start = 0;
        end = 0;
        for (i = 0; i < count + 1; i++) {
            end = pos[i];
            // 判断是否已到达字符串的最后一个子串, 让终点等于字符串的长度
            if (end == 0) {
                end = len;
            }
            if (end > start) {
                // 获取下一个分割点的起始位置
                strs[i] = Integer.parseInt(src.substring(start, end));
                start = end + 1;
            } else {
                strs[i] = 0;
            }
        }

        return strs;
    }

    /**
     * 将字符串数组根据分隔符整合成一个字符串
     *
     * @param strArray
     * @param split
     * @return
     */
    public static String arrayToString(String[] strArray, String split) {
        String result = "";
        if (strArray == null) {
            return result;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strArray.length; i++) {
            sb.append(strArray[i]);
            if (i != strArray.length - 1) {
                sb.append(split);
            }
        }
        result = sb.toString();
        return result;
    }

    /**
     * 将整型数组根据分隔符整合成一个字符串
     *
     * @param intArray
     * @param split
     * @return
     */
    public static String arrayToString(int[] intArray, String split) {
        String result = "";
        if (intArray == null) {
            return result;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < intArray.length; i++) {
            sb.append(intArray[i]);
            if (i != intArray.length - 1) {
                sb.append(split);
            }
        }
        result = sb.toString();
        return result;
    }

    /**
     * 字符串转浮点型数
     *
     * @param str
     * @return 浮点型数, 如果字符串为空对象或字符串有效长度为0则返回0.0
     */
    public static float parseToFloat(String str) {
        float result = (float) 0.0;

        if (str == null) {
            // 空对象直接返回0.0
            return result;
        }

        String tmpStr = str.trim();
        if (tmpStr.length() <= 0) {
            // 空字符串直接返回0.0
            return result;
        }

        result = Float.parseFloat(tmpStr);
        return result;
    }

    /**
     * 把价格字符串转成长整数（小数点后移两位，比如1.11 转成111；1.1转成100；1转成100）
     *
     * @param priceStr
     * @return 转换后的整数
     */
    public static long priceStrToLong(String priceStr) {
        long result = 0;
        long intNum = 0, decimalsNum = 0;
        String[] strArr;

        if (priceStr == null || priceStr.length() <= 0) {
            return result;
        }

        strArr = priceStr.split("\\.");
        if (strArr != null && strArr.length > 0) {
            intNum = ConvertUtils.stringToInt(strArr[0]) * 100;
            if (strArr.length >= 2 && strArr[1].length() > 0) {
                if (strArr[1].length() > 2) {
                    decimalsNum = ConvertUtils.stringToInt(strArr[1].substring(0, 2));
                } else if (strArr[1].length() == 1) {
                    decimalsNum = ConvertUtils.stringToInt(strArr[1]) * 10;
                } else {
                    decimalsNum = ConvertUtils.stringToInt(strArr[1]);
                }
            }
        } else {
            return result;
        }

        result = intNum + decimalsNum;
        return result;
    }

    /**
     * 把长整数转成有两位小数点的价格字符串（例如1转成0.01；10转成0.10；100转成1.00）
     *
     * @param priceNum
     * @return
     */
    public static String longToPriceStr(long priceNum) {
        String result = "";
        String priceStr = "";
        if (priceNum < 0) {
            long tmpNum = 0 - priceNum;
            priceStr = String.format("%03d", tmpNum);
        } else {
            priceStr = String.format("%03d", priceNum);
        }

        result = priceStr.substring(0, priceStr.length() - 2) + "."
                + priceStr.substring(priceStr.length() - 2, priceStr.length());

        if (priceNum < 0) {
            result = "-" + result;
        }
        return result;
    }

    /**
     * 测试输入的字符是否为GB码
     *
     * @param ch
     * @return true: 是GB码; false: 不是GB码
     */
    public static boolean isGBCode(char ch) {
        if (ch > 0x80) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 计算字符串实际字符长度，汉字算2个字符长度，遇到换行时，整行填充长度为maxNum
     *
     * @param str
     * @param maxNum 每行最多显示的字符数（GB码算2个字符）
     * @return 需要显示的字符数
     */
    public static int getRealCharNum(String str, int maxNum) {
        int i, codeNum = 0;
        char ch;

        if (str == null || str.length() == 0) {
            return 0;
        }

        for (i = 0; i < str.length(); i++) {
            ch = str.charAt(i);

            if (isGBCode(ch)) {
                codeNum += 2;
            } else if (ch == '\n' || ch == '\r') {
                // 如遇到'\r','\n',"\r\n"时换行处理
                if (ch == '\r' && (str.length() > i + 1 && str.charAt(i + 1) == '\n')) {
                    i++;
                }
                codeNum += maxNum - codeNum % maxNum;
            } else {
                codeNum++;
            }
        }

        return codeNum;
    }

    /**
     * 从字符串中截取相应字符长度的子串（汉字算2个字符长度，遇到换行时，整行填充长度为maxNum）
     *
     * @param str 完整的字符串
     * @param len 需要截取的子串字符长度（GB码算2个字符）
     * @return 截取的子串
     */
    public static String getRealCharNumStr(String str, int len) {
        int i, codeNum = 0;
        char ch;
        String subStr = null;

        if (str == null || str.length() == 0) {
            return null;
        }

        for (i = 0; i < str.length(); i++) {
            ch = str.charAt(i);

            if (isGBCode(ch)) {
                codeNum += 2;
            } else if (ch == '\n' || ch == '\r') {
                // 如遇到'\r','\n',"\r\n"时换行处理
                if (ch == '\r' && (str.length() > i + 1 && str.charAt(i + 1) == '\n')) {
                    i++;
                }
            } else {
                codeNum++;
            }

            if (codeNum == len || codeNum > len) {
                subStr = str.substring(0, i);
                break;
            }
        }
        if (codeNum < len) {
            subStr = str;
        }

        return subStr;
    }

    /**
     * 检查输入的字符串是否是有效的无符号数, 例如: 1.23
     *
     * @param src
     * @param count
     * @return 字符串格式是否正确
     */
    public static boolean checkFloatNumber(String src, int count) {
        if (src == null || src.length() == 0) {
            return true;
        }
        int len = src.length();
        int num = findCharNum(src, '.', len);
        if (num > 1) {
            return false;
        } else if (num == 1) {
            // 小数点前后都必须有数字
            int pos = findCharPos(src, '.', 0, len);
            if ((pos == 0) || (pos == len - 1)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 替换空格
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {

        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (null == str || str.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 指定的字符串 {@link String#trim()} 之后是否为空
     *
     * @param s 字符串
     * @return true 表示为空
     */
    public static boolean isTrimEmpty(final String s) {
        return (s == null || s.trim().length() == 0);
    }

    /**
     * 检查输入的字符串是否是有效的数值型(允许带小数点)
     *
     * @param value 数值型字符串, 例如 "2.0"
     * @return
     */
    public static boolean isNumeric(String value) {
        if (value == null || value.length() == 0) {
            return true;
        }
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            // 字符串的内容只允许是数字或是小数点
            if ((chars[i] < '0' || chars[i] > '9') && chars[i] != '.') {
                return false;
            }
        }
        if (value.contains(".")) {
            String[] strs = splitToArray(value, ".");
            // 小数点数量超过1个说明格式有错误
            if (strs.length > 2) {
                return false;
            }
            for (int i = 0; i < strs.length; i++) {
                // 小数点前后都不允许是空白的
                if (strs[i].length() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断手机格式是否正确
     *
     * @param mobiles
     * @return 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186
     * 电信：133、153、180、189、（1349卫通）
     * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
     */
    public static boolean isMobileNum(String mobiles) {
        //"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1][34578]\\d{9}";
        if (isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }
    //16->2
    public static String hexString2binaryString(String hexString) {
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    //2->16
    public static String binaryString2hexString(String bString) {
        if (bString.length() % 8 != 0) {
            String sbuwei = "00000000";
            bString = sbuwei.substring(0, sbuwei.length() - bString.length() % 8) + bString;
        }
        StringBuilder tmp = new StringBuilder();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }
    /**
     * 判断是否为奇数，位运算，最后一位是1则为奇数，为0是偶数
     *
     * @param num 传入的int数据
     * @return 如果为奇数，返回true；如果为偶数返回false
     */
    public static boolean isOdd(int num) {
        return (num & 0x1) == 1;
    }

    /**
     * hex字符串转字节数组
     *
     * @param hexString 传入的数据
     * @return 转换后的结果
     */
    public static byte[] hexString2ByteArray(String hexString) {
        int len = hexString.length();
        byte[] result;
        if (isOdd(len)) {
            //奇数
            len++;
            result = new byte[(len / 2)];
            hexString = "0" + hexString;
        } else {
            //偶数
            result = new byte[(len / 2)];
        }
        int j = 0;
        for (int i = 0; i < len; i += 2) {
            result[j] = hexString2Byte(hexString.substring(i, i + 2));
            j++;
        }
        return result;
    }

	/**
	 * 16进制字符串转换为byte[]
	 * 
	 * @param hexString
	 * @return
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase().replace(" ", "");
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

    /**
     * 16进制字符串转byte
     *
     * @param hexString 传入的十六进制字符串
     * @return 转换后的结果
     */
    public static byte hexString2Byte(String hexString) {
        return (byte) Integer.parseInt(hexString, 16);
    }

    /**
     * Byte 转 十六进制字符串
     *
     * @param hexByte 传入的数据
     * @return 转换后的结果
     */
    public static String byte2HexString(Byte hexByte) {
        return String.format("%02x", hexByte).toUpperCase();
    }

    /**
     * 字节数组转hex字符串
     *
     * @param hexbytearray 传入的数据
     * @return 转换后的结果
     */
    public static String byteArray2HexString(byte[] hexbytearray) {

        return byteArray2HexString(hexbytearray, 0, hexbytearray.length);
    }

    /**
     * 字节数组转转hex字符串
     *
     * @param hexbytearray 传入的数据
     * @return 转换后的结果
     */
    public static String byteArray2HexString(byte[] hexbytearray, int beginIndex, int endIndex)//字节数组转转hex字符串，可选长度
    {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = beginIndex; i < endIndex; i++) {
            strBuilder.append(byte2HexString(hexbytearray[i]));
        }
        return strBuilder.toString();
    }

    /**
     * 16进制字符串转int
     *
     * @param hexString 传入的十六进制字符串
     * @return 转换后的结果
     */
    public static int hexString2Int(String hexString) {
        return Integer.parseInt(hexString, 16);
    }

	/**
	 * byte[]转换成16进制字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
			if (i != src.length -1) {
				stringBuilder.append(" ");
			}
		}
		return stringBuilder.toString();
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}


    /**
     * 判断传入的字符串对象是否为空字符串.<br>
     * (ps:会自动过滤字符串前后的空格，且当字符串为空字符时，也返回true)
     * @param str 字符串
     * @return boolean
     */
    public static boolean isBlank(final String str) {
        return str == null || "".equals(str.trim());
    }
    public static boolean isNotBlank(final String str) {
        return str != null && !"".equals(str.trim());
    }

    /**
     * 将字符串转成byte数组，默认utf-8
     * @param str 字符串
     * @return 字节数组
     */
    public static byte[] stringToBytes(String str){
        return stringToBytes(str, "UTF-8");
    }

    /**
     * 羌字符串转成指定编码的字节数组
     * @param str 字符串
     * @param charsetName 编码格式
     * @return 字节数组
     */
    public static byte[] stringToBytes(String str, String charsetName) {
        try {
            if (str == null || isBlank(charsetName)) {
                throw new IllegalArgumentException("字符串或字符集不能为空");
            }
            return str.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将字节数组转成指定编码的字符串
     * @param bytes byte字节数组
     * @param charsetName 编码格式
     * @return 字符串
     */
    public static String bytesToString(byte[] bytes, String charsetName) {
        try {
            String str = new String(bytes, charsetName);
            return str;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将字节数组转成字符串，默认UTF-8编码
     * @param bytes byte字节数组
     * @return 字符串
     */
    public static String bytesToString(byte[] bytes) {
        return bytesToString(bytes, "UTF-8");
    }

    /**
     * 字符串是否有数据
     * @param str 字符串
     * @return 是否有数据
     */
    public static boolean hasLength(String str) {
        return str != null && str.length() > 0;
    }


    public static boolean hasText(String str) {
        if (!hasLength(str)) {
            return false;
        } else {
            int strLen = str.length();

            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * url编码
     * @param str
     * @return
     */
    public static String encodeUrl(String str){
        String s = null;
        try {
            s = URLEncoder.encode(str, FileConstants.ENCODE_UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return s;
    }

    /**
     * url解码
     * @param str
     * @return
     */
    public static String decodeUrl(String str){
        String s = null;
        try {
            s = URLDecoder.decode(str, FileConstants.ENCODE_UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 字符串分隔 StringTokenizer效率是三种分隔方法中最快的
     * @param str 待分割字符串
     * @param split 分隔符
     * @return
     */
    public static String[] split(String str, String split){
        if(str == null){
            return new String[]{};
        }
        StringTokenizer token = new StringTokenizer(str,split);
        String[] strArr = new String[token.countTokens()];
        int i = 0;
        while(token.hasMoreElements()){
            strArr[i] = token.nextElement().toString();
            i++;
        }
        return strArr;
    }

    /**
     * 字符串拼接
     * @param sign
     * @param strArr
     * @return
     */
    @SuppressWarnings("NewApi")
    public static String joinStr(String sign, String... strArr){
        Optional<String> optional  = Arrays.stream(strArr).filter(Objects::nonNull
        ).reduce((a, b) -> a + sign + b);
        return optional.orElse("");
    }

    /**
     * 两个二进制字符串数据相加
     * 输入: a = "1010", b = "1011"
     * 输出: "10101"
     * @param a 数据a
     * @param b 数据b
     * @return 相加后的二进制字符串
     */
    public static String addBinary(String a, String b) {
        int carry = 0;
        int sum = 0;
        int opa = 0;
        int opb = 0;
        StringBuilder result = new StringBuilder();
        while (a.length() != b.length()) {
            if (a.length() > b.length()) {
                b = "0" + b;
            } else {
                a = "0" + a;
            }
        }
        for (int i = a.length() - 1; i >= 0; i--) {
            opa = a.charAt(i) - '0';
            opb = b.charAt(i) - '0';
            sum = opa + opb + carry;
            if (sum >= 2) {
                result.append((char) (sum - 2 + '0'));
                carry = 1;
            } else {
                result.append((char) (sum + '0'));
                carry = 0;
            }
        }
        if (carry == 1) {
            result.append("1");
        }
        return result.reverse().toString();
    }

    public static int chineseLength(String str) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        if (!isEmpty(str)) {
            for (int i = 0; i < str.length(); i++) {
                String temp = str.substring(i, i + 1);
                if (temp.matches(chinese)) {
                    valueLength += 2;
                }
            }
        }
        return valueLength;
    }

    public static int strLength(String str) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        if (!isEmpty(str)) {
            for (int i = 0; i < str.length(); i++) {
                String temp = str.substring(i, i + 1);
                if (temp.matches(chinese)) {
                    valueLength += 2;
                } else {
                    valueLength += 1;
                }
            }
        }
        return valueLength;
    }

    public static int subStringLength(String str, int maxL) {
        int currentIndex = 0;
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < str.length(); i++) {
            String temp = str.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
            if (valueLength >= maxL) {
                currentIndex = i;
                break;
            }
        }
        return currentIndex;
    }

    public static Boolean isChinese(String str) {
        Boolean isChinese = true;
        String chinese = "[\u0391-\uFFE5]";
        if (!isEmpty(str)) {
            for (int i = 0; i < str.length(); i++) {
                String temp = str.substring(i, i + 1);
                isChinese = temp.matches(chinese);
            }
        }
        return isChinese;
    }

    public static Boolean isContainChinese(String str) {
        Boolean isChinese = false;
        String chinese = "[\u0391-\uFFE5]";
        if (!isEmpty(str)) {
            for (int i = 0; i < str.length(); i++) {
                String temp = str.substring(i, i + 1);
                isChinese = temp.matches(chinese);
            }
        }
        return isChinese;
    }

    /**
     * 字符串的首字符大写
     *
     * @param s 字符串
     * @return 处理之后的字符串
     */
    public static String upperFirstLetter(final String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        if (!Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }

    /**
     * 字符串的首字符小写
     *
     * @param s 字符串
     * @return 处理之后的字符串
     */
    public static String lowerFirstLetter(final String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        if (!Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
    }

    /**
     * 字符串反转
     *
     * @param s 字符串
     * @return 反转之后的字符串
     */
    public static String reverse(final String s) {
        if (s == null) {
            return "";
        }
        int len = s.length();
        if (len <= 1) {
            return s;
        }
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        char c;
        for (int i = 0; i < mid; ++i) {
            c = chars[i];
            chars[i] = chars[len - i - 1];
            chars[len - i - 1] = c;
        }
        return new String(chars);
    }

    private static final char[] BASE_64_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z', '+', '/', };

    /**
     * 将数字转换成六十四进制字符串
     *
     * @param number 数字
     * @return 字符串
     */
    public static String long2Base64String(long number) {
        char[] buf = new char[64];
        int charPos = 64;
        int radix = 1 << 6;
        long mask = radix - 1L; // 截取后几位，在 [0,63] 之间
        do {
            buf[--charPos] = BASE_64_DIGITS[(int) (number & mask)];
            number >>>= 6;
        } while (number != 0);
        return new String(buf, charPos, (64 - charPos));
    }

    /**
     * 将六十四进制字符串还原回数字
     *
     * @param base64String 六十四进制字符串
     * @return 数字
     */
    public static long base64String2Long(String base64String) {
        long result = 0;
        int length = base64String.length();
        for (int i = length - 1; i >= 0; i--) {
            for (int j = 0; j < BASE_64_DIGITS.length; j++) {
                if (base64String.charAt(i) == BASE_64_DIGITS[j]) {
                    result += ((long) j) << 6 * (base64String.length() - 1 - i);
                }
            }
        }
        return result;
    }

    public static interface StringFunction<F> {
        /**
         * 将输入类型转换成输出类型
         *
         * @param from 输入
         * @return     输出
         */
        String apply(F from);
    }

    /**
     * 使用指定的字符串将容器中的元素拼接起来
     *
     * @param c         容器
     * @param connector 连接的字符串
     * @param <T>       容器元素类型
     * @return          拼接结果
     */
    public static <T> String joint(Collection<T> c, String connector) {
        return joint(c, connector, new StringFunction<T>() {
            @Override
            public String apply(T from) {
                return from.toString();
            }
        });
    }

    /**
     * 将传入的列表按照指定的格式拼接起来
     *
     * @param c         容器
     * @param connector 连接的字符串
     * @param function  对象到字符串映射格式
     * @param <T>       对象类型
     * @return          拼接结果
     */
    public static <T> String joint(Collection<T> c, String connector, StringFunction<T> function) {
        if (c == null || c.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        int size = c.size();
        for (T element : c) {
            if (count++ != size - 1) {
                sb.append(function.apply(element)).append(connector);
            } else {
                sb.append(function.apply(element));
            }
        }
        return sb.toString();
    }
}
