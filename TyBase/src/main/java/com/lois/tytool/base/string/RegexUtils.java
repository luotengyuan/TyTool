package com.lois.tytool.base.string;

import com.lois.tytool.base.string.StringUtils;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 正则表达式工具类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class RegexUtils {

    private final static String PHONE_NUMBER = "^(13[0-9]|14[579]|15[0-3,5-9]|16[0-9]|17[0135678]|18[0-9]|19[89])\\d{8}$";

    private final static String EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    private final static String HK_PHONE_NUMBER = "^(5|6|8|9)\\d{7}$";

    private final static String POSITIVE_INTEGER = "^\\d+$";

    private final static String MONEY = "^(\\d+(?:\\.\\d+)?)$";

    private final static String ID_CARD = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$";

    public final static String IP = "^(25[0-5]|2[0-4][0-9]|1{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|1{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|1{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|1{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$";

    public final static String PORT = "^6553[0-5]|655[0-2][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[1-5][0-9]{4}|[1-9][0-9]{0,3}$";

    public final static String ALL_CHINESE = "^[\\u4e00-\\u9fa5]*$";

    private final static String URL = "^(([hH][tT]{2}[pP][sS]?)|([fF][tT][pP]))\\:\\/\\/[\\w-]+\\.\\w{2,4}(\\/.*)?$";

    private final static String VehicleNumber = "^[京津晋冀蒙辽吉黑沪苏浙皖闽赣鲁豫鄂湘粤桂琼川贵云藏陕甘青宁新渝]?[A-Z][A-HJ-NP-Z0-9学挂港澳练]{5}$";

    /**
     * 校验邮箱
     * @param str
     * @return
     */
    public static boolean isEmail(String str){
        return checkStr(EMAIL, str);
    }

    /**
     * 校验身份证
     * @param str
     * @return
     */
    public static boolean isIdCard(String str){
        return checkStr(ID_CARD, str);
    }

    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     *
     * @param str
     * @return
     */
    public static boolean isMobilePhone_cn(String str){
        return checkStr(PHONE_NUMBER, str);
    }

    /**
     * 校验香港
     * @param str
     * @return
     */
    public static boolean isMobilePhone_hk(String str){
        return checkStr(HK_PHONE_NUMBER, str);
    }

    /**
     * 校验正整数
     * @param str
     * @return
     */
    public static boolean isPlusInteger(String str){
        return checkStr(POSITIVE_INTEGER, str);
    }

    /**
     * 校验money
     * @param str
     * @return
     */
    public static boolean isMoney(String str){
        return checkStr(MONEY, str);
    }

    /**
     * 是否IP
     * @param value
     * @return
     */
    public static boolean isIP(String value) {
        return checkStr(IP, value);
    }

    /**
     * 是否端口号
     * @param value
     * @return
     */
    public static boolean isPort(String value) {
        return checkStr(PORT, value);
    }

    /**
     * 是否URL
     * @param value
     * @return
     */
    public static boolean isUrl(String value) {
        return checkStr(URL, value.toLowerCase());
    }

    /**
     * 是否车牌
     * @param value
     * @return
     */
    public static boolean isVehicleNumber(String value) {
        return checkStr(VehicleNumber, value.toLowerCase());
    }

    /**
     * 只含字母和数字
     *
     * @param data 可能只包含字母和数字的字符串
     * @return 是否只包含字母和数字
     */
    public static boolean isNumberLetter(String data) {
        String expr = "^[A-Za-z0-9]+$";
        return checkStr(expr, data);
    }

    /**
     * 只含数字
     *
     * @param data 可能只包含数字的字符串
     * @return 是否只包含数字
     */
    public static boolean isNumber(String data) {
        String expr = "^[0-9]+$";
        return checkStr(expr, data);
    }

    /**
     * 只含字母
     *
     * @param data 可能只包含字母的字符串
     * @return 是否只包含字母
     */
    public static boolean isLetter(String data) {
        String expr = "^[A-Za-z]+$";
        return checkStr(expr, data);
    }

    /**
     * 只是中文
     *
     * @param data 可能是中文的字符串
     * @return 是否只是中文
     */
    public static boolean isChinese(String data) {
        String expr = "^[\u0391-\uFFE5]+$";
        return checkStr(expr, data);
    }

    /**
     * 包含中文
     *
     * @param data 可能包含中文的字符串
     * @return 是否包含中文
     */
    public static boolean isContainChinese(String data) {
        String chinese = "[\u0391-\uFFE5]";
        if (!StringUtils.isEmpty(data)) {
            for (int i = 0; i < data.length(); i++) {
                String temp = data.substring(i, i + 1);
                boolean flag = temp.matches(chinese);
                if (flag) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 小数点位数
     *
     * @param data   可能包含小数点的字符串
     * @param length 小数点后的长度
     * @return 是否小数点后有length位数字
     */
    public static boolean isDianWeiShu(String data, int length) {
        String expr = "^[1-9][0-9]+\\.[0-9]{" + length + "}$";
        return checkStr(expr, data);
    }

    /**
     * 邮政编码验证
     *
     * @param data 可能包含邮政编码的字符串
     * @return 是否是邮政编码
     */
    public static boolean isPostCode(String data) {
        String expr = "^[0-9]{6,10}";
        return checkStr(expr, data);
    }

    /**
     * 长度验证
     *
     * @param data   源字符串
     * @param length 期望长度
     * @return 是否是期望长度
     */
    public static boolean isLength(String data, int length) {
        return data != null && data.length() == length;
    }

    /**
     * 校验方法
     * @param regex
     * @param str
     * @return
     */
    private static boolean checkStr(String regex, String str){
        if(regex == null || str == null){
            return false;
        }
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
