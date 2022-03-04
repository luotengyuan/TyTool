package com.lois.tytool.basej.string;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * @Description 中文字符和拼音之间的转换
 * @Author Luo.T.Y
 * @Date 2022/2/28
 * @Time 10:06
 */
public class Pinyin4jUtils {

    public enum Type {
        UPPERCASE,
        LOWERCASE,
        FIRST_UPPER
    }

    /**
     * 将汉字转换为拼音的全拼，非汉字的原样输出
     * @param str 待转换字符串
     * @return 转换后拼音字符串
     */
    public static String getQuanPin(String str) {
        return getQuanPin(str, Type.FIRST_UPPER, "", false);
    }

    /**
     * 将汉字转换为拼音的全拼的大写，非汉字的原样输出
     * @param str 待转换字符串
     * @param type 输出大小写样式
     * @return 转换后拼音字符串
     */
    public static String getQuanPin(String str, Type type) {
        return getQuanPin(str, type, "", false);
    }

    /**
     * 将汉字转换为拼音的全拼，非汉字的原样输出
     * @param str 待转换字符串
     * @param gap 间隔填充字符
     * @return 转换后拼音字符串
     */
    public static String getQuanPin(String str, String gap) {
        return getQuanPin(str, Type.FIRST_UPPER, gap, false);
    }

    /**
     * 将汉字转换为拼音的全拼，非汉字的原样输出
     * @param str 待转换字符串
     * @param type 输出大小写样式
     * @param gap 间隔填充字符
     * @return 转换后拼音字符串
     */
    public static String getQuanPin(String str, Type type, String gap) {
        return getQuanPin(str, type, gap, false);
    }

    /**
     * 将汉字转换为拼音的全拼，非汉字的原样输出
     * @param str 待转换字符串
     * @param type 输出大小写样式
     * @param gap 间隔填充字符
     * @param withTone 是否带声调
     * @return 转换后拼音字符串
     */
    public static String getQuanPin(String str, Type type, String gap, boolean withTone) {
        StringBuilder sb = new StringBuilder();

        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        if (type == Type.UPPERCASE) {
            defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        } else {
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        }
        if (withTone) {
            defaultFormat.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
        } else {
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        }

        for (int i = 0; i < str.length(); i++) {
            char s = str.charAt(i);

            /**
             * PinyinHelper.toHanyuPinyinStringArray说明：
             * 1、参数传中文，返回值是这个汉字的小写拼音+这个汉字是几声。比如：参数传 "汉 "，return的String[ ] 就是 [han4]
             * 2、当传字母时，返回值是null。
             * 注意：toHanyuPinyinStringArray接收的参数是char，意思就是说一次只能转换一个，
             * 比如“美”是string，toHanyuPinyinStringArray不能直接接收，
             * 每次只能传一个，返回的那个String数组里肯定只有一个元素。
             */
            String[] s1 = null;
            try {
                s1 = PinyinHelper.toHanyuPinyinStringArray(s, defaultFormat);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }

            if (null == s1 || s1.length <= 0) {
                sb.append(s);
            } else {
                if (type == Type.FIRST_UPPER) {
                    char[] ch = s1[0].toCharArray();
                    if (ch != null && ch.length > 0 && ch[0] >= 'a' && ch[0] <= 'z') {
                        ch[0] = (char) (ch[0] - 32);
                    }
                    sb.append(new String(ch));
                } else {
                    sb.append(s1[0]);
                }
                if (i != str.length() - 1) {
                    sb.append(gap);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 返回中文的首字母，非汉字的原样输出
     * @param str 待转换字符串
     * @return 转换后拼音首字母
     */
    public static String getPinYinHeadChar(String str) {
        return getPinYinHeadChar(str, Type.FIRST_UPPER, "");
    }

    /**
     * 返回中文的首字母，非汉字的原样输出
     * @param str 待转换字符串
     * @param type 输出大小写样式
     * @return 转换后拼音首字母
     */
    public static String getPinYinHeadChar(String str, Type type) {
        return getPinYinHeadChar(str, type, "");
    }

    /**
     * 返回中文的首字母，非汉字的原样输出
     * @param str 待转换字符串
     * @param gap 间隔填充字符
     * @return 转换后拼音首字母
     */
    public static String getPinYinHeadChar(String str, String gap) {
        return getPinYinHeadChar(str, Type.FIRST_UPPER, gap);
    }

    /**
     * 返回中文的首字母，非汉字的原样输出
     * @param str 待转换字符串
     * @param type 输出大小写样式
     * @param gap 间隔填充字符
     * @return 转换后拼音首字母
     */
    public static String getPinYinHeadChar(String str, Type type, String gap) {
        StringBuilder sb = new StringBuilder();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        if (type == Type.UPPERCASE) {
            defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        } else {
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        }

        for (int i = 0; i < str.length(); i++) {
            char s = str.charAt(i);

            /**
             * PinyinHelper.toHanyuPinyinStringArray说明：
             * 1、参数传中文，返回值是这个汉字的小写拼音+这个汉字是几声。比如：参数传 "汉 "，return的String[ ] 就是 [han4]
             * 2、当传字母时，返回值是null。
             * 注意：toHanyuPinyinStringArray接收的参数是char，意思就是说一次只能转换一个，
             * 比如“美”是string，toHanyuPinyinStringArray不能直接接收，
             * 每次只能传一个，返回的那个String数组里肯定只有一个元素。
             */
            String[] s1 = null;
            try {
                s1 = PinyinHelper.toHanyuPinyinStringArray(s, defaultFormat);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }

            if (null == s1 || s1.length <= 0) {
                sb.append(s);
            } else {
                char[] ch = s1[0].toCharArray();
                if (ch != null && ch.length > 0) {
                    sb.append(ch[0]);
                }
                if (i != str.length() - 1) {
                    sb.append(gap);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 汉字转换位汉语全拼，英文字符不变，特殊字符丢失 支持多音字，生成方式如（重当参:zhongdangcen,zhongdangcan,chongdangcen ,chongdangshen,zhongdangshen,chongdangcan）
     *
     * @param str 汉字
     * @return 拼音
     */
    public static List<String> getPinYinMultiple(String str) {
        return getPinYinMultiple(str, Type.FIRST_UPPER, false);
    }

    /**
     * 汉字转换位汉语全拼，英文字符不变，特殊字符丢失 支持多音字，生成方式如（重当参:zhongdangcen,zhongdangcan,chongdangcen ,chongdangshen,zhongdangshen,chongdangcan）
     *
     * @param str 汉字
     * @param type 输出大小写样式
     * @return 拼音
     */
    public static List<String> getPinYinMultiple(String str, Type type) {
        return getPinYinMultiple(str, type, false);
    }

    /**
     * 汉字转换位汉语全拼，英文字符不变，特殊字符丢失 支持多音字，生成方式如（重当参:zhongdangcen,zhongdangcan,chongdangcen ,chongdangshen,zhongdangshen,chongdangcan）
     *
     * @param str 汉字
     * @param withTone 是否带声调
     * @return 拼音
     */
    public static List<String> getPinYinMultiple(String str, boolean withTone) {
        return getPinYinMultiple(str, Type.FIRST_UPPER, withTone);
    }

    /**
     * 汉字转换位汉语全拼，英文字符不变，特殊字符丢失 支持多音字，生成方式如（重当参:zhongdangcen,zhongdangcan,chongdangcen ,chongdangshen,zhongdangshen,chongdangcan）
     *
     * @param str 汉字
     * @param type 输出大小写样式
     * @param withTone 是否带声调
     * @return 拼音
     */
    public static List<String> getPinYinMultiple(String str, Type type, boolean withTone) {
        StringBuilder sb = new StringBuilder();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        if (type == Type.UPPERCASE) {
            defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        } else {
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        }
        if (withTone) {
            defaultFormat.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
        } else {
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        }
        for (int i = 0; i < str.length(); i++) {
            char s = str.charAt(i);
            /**
             * PinyinHelper.toHanyuPinyinStringArray说明：
             * 1、参数传中文，返回值是这个汉字的小写拼音+这个汉字是几声。比如：参数传 "汉 "，return的String[ ] 就是 [han4]
             * 2、当传字母时，返回值是null。
             * 注意：toHanyuPinyinStringArray接收的参数是char，意思就是说一次只能转换一个，
             * 比如“美”是string，toHanyuPinyinStringArray不能直接接收，
             * 每次只能传一个，返回的那个String数组里肯定只有一个元素。
             */
            String[] s1 = null;
            try {
                s1 = PinyinHelper.toHanyuPinyinStringArray(s, defaultFormat);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }

            if (null == s1 || s1.length <= 0) {
                sb.append(s);
            } else {
                for (int j = 0; j < s1.length; j++) {
                    if (type == Type.FIRST_UPPER) {
                        char[] ch = s1[j].toCharArray();
                        if (ch.length > 0 && ch[0] >= 'a' && ch[0] <= 'z') {
                            ch[0] = (char) (ch[0] - 32);
                        }
                        sb.append(new String(ch));
                    } else {
                        sb.append(s1[j]);
                    }
                    if (j != s1.length - 1) {
                        sb.append(",");
                    }
                }
            }
            sb.append(" ");
        }
        return parseTheChineseByObject(discountTheChinese(sb.toString()));
    }

    /**
     * 去除多音字重复数据
     *
     * @param theStr 组装后的字符串
     * @return 解析后的list
     */
    private static List<Map<String, Integer>> discountTheChinese(String theStr) {
        // 去除重复拼音后的拼音列表
        List<Map<String, Integer>> mapList = new ArrayList<Map<String, Integer>>();
        // 用于处理每个字的多音字，去掉重复
        Map<String, Integer> onlyOne = null;
        String[] firsts = theStr.split(" ");
        // 读出每个汉字的拼音
        for (String str : firsts) {
            onlyOne = new Hashtable<String, Integer>();
            String[] china = str.split(",");
            // 多音字处理
            for (String s : china) {
                Integer count = onlyOne.get(s);
                if (count == null) {
                    onlyOne.put(s, 1);
                } else {
                    onlyOne.remove(s);
                    count++;
                    onlyOne.put(s, count);
                }
            }
            mapList.add(onlyOne);
        }
        return mapList;
    }

    /**
     * 解析并组合拼音，对象合并方案
     * @return 多音字拼音组合
     */
    private static List<String> parseTheChineseByObject(List<Map<String, Integer>> list) {
        // 用于统计每一次,集合组合数据
        Map<String, Integer> first = null;
        // 遍历每一组集合
        for (int i = 0; i < list.size(); i++) {
            // 每一组集合与上一次组合的Map
            Map<String, Integer> temp = new Hashtable<String, Integer>();
            // 第一次循环，first为空
            if (first != null) {
                // 取出上次组合与此次集合的字符，并保存
                for (String s : first.keySet()) {
                    for (String s1 : list.get(i).keySet()) {
                        String str = s + s1;
                        temp.put(str, 1);
                    }
                }
                // 清理上一次组合数据
                if (temp.size() > 0) {
                    first.clear();
                }
            } else {
                for (String s : list.get(i).keySet()) {
                    temp.put(s, 1);
                }
            }
            // 保存组合数据以便下次循环使用
            if (temp.size() > 0) {
                first = temp;
            }
        }
        List<String> retList = new ArrayList<>();
        if (first != null) {
            // 遍历取出组合字符串
            retList.addAll(first.keySet());
        }
        return retList;
    }

}