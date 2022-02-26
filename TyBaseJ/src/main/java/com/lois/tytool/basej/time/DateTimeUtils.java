package com.lois.tytool.basej.time;

import com.lois.tytool.basej.constant.TimeConstants;
import com.lois.tytool.basej.math.ConvertUtils;
import com.lois.tytool.basej.string.StringUtils;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @Description 时间相关工具类
 * @Author Luo.T.Y
 * @Date 2017-09-22
 * @Time 14:13
 */
public class DateTimeUtils {
    private static Logger logger = LoggerFactory.getLogger(DateTimeUtils.class);

    public final static String DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public final static String DATE_STR = "yyyy-MM-dd";

    public static final String DATETIME_MS = "yyyyMMddHHmmssSSS";

    public static final String DATE_SLASH_STR = "yyyy/MM/dd";

    public final static int SECOND = 1000;

    public final static int MINUTE = 60 * SECOND;

    public final static int HOUR = 60 * MINUTE;

    public final static int DAY = 24 * HOUR;

    /**
     * 每月天数
     */
    static final int MONTH_DAY[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private static final int[]    ZODIAC_FLAGS = {20, 19, 21, 21, 21, 22, 23, 23, 23, 24, 23, 22};
    private static final String[] ZODIAC       = {
            "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座",
            "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"
    };

    private static final String[] CHINESE_ZODIAC =
            {"猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊"};

    public static long now() {
        return System.currentTimeMillis();
    }

    public static String nowString(final DateFormat format) {
        return toString(System.currentTimeMillis(), format);
    }

    public static Date nowDate() {
        return new Date();
    }

    /**
     * 获取指定月份的月天数
     *
     * @param year : 指定的年份 month: 指定的月份
     * @return 月天数
     */
    public static int getDay(int year, int month) {
        if (month > 12 || month == 0) {
            return 30;
        }
        if (month == 2) {
            // 判断是否为闰年
            if ((year % 4) == 0) {
                return 29;
            } else {
                return 28;
            }
        } else {
            return MONTH_DAY[month - 1];
        }
    }

    /**
     * 获取从2000年到date一共有多少天
     *
     * @param year  ：年
     * @param month ：月份
     * @param day   ：日期
     * @return 总共天数
     */
    public static int getAllDays(int year, int month, int day) {
        int yearDays = 0, monthDays = 0, allDays = 0;

        if (year == 0) {
            yearDays = 0;
        } else {
            yearDays = year * 365 + ((year - 1) / 4 + 1) * 1;
        }
        switch (month) {
            case 1:
                monthDays = 0;
                break;
            case 2:
                monthDays = 31;
                break;
            case 3:
                monthDays = 31 + getDay(year, 2);
                break;
            case 4:
                monthDays = 62 + getDay(year, 2);
                break;
            case 5:
                monthDays = 92 + getDay(year, 2);
                break;
            case 6:
                monthDays = 123 + getDay(year, 2);
                break;
            case 7:
                monthDays = 153 + getDay(year, 2);
                break;
            case 8:
                monthDays = 184 + getDay(year, 2);
                break;
            case 9:
                monthDays = 215 + getDay(year, 2);
                break;
            case 10:
                monthDays = 245 + getDay(year, 2);
                break;
            case 11:
                monthDays = 276 + getDay(year, 2);
                break;
            case 12:
                monthDays = 306 + getDay(year, 2);
                break;
            default:
                break;
        }
        allDays = yearDays + monthDays + day;
        return allDays;
    }

    /**
     * 获取两个输入时间的差值(YYYY-MM-dd HH:mm:ss格式输入)
     *
     * @return 单位s
     */
    public static long getDateTimeDiffer(String startTime, String endTime) {

        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin;
        Date end;
        long between = 0;
        try {
            begin = dfs.parse(startTime);
            end = dfs.parse(endTime);
            between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return Math.abs(between);
    }

    /**
     * 获取当前日期和时间
     *
     * @return YYYY-MM-dd HH:mm:ss格式输出时间
     */
    public static String getDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(new Date());
    }

    /**
     * 获取当前日期和时间
     *
     * @return YYYY-MM-dd HH:mm:ss格式输出时间
     */
    public static String getDateTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(new Date(time));
    }

    /**
     * 获取当前日期和时间
     *
     * @return YYYY-MM-dd_HH-mm-ss格式输出时间
     */
    public static String getDateTimeFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return formatter.format(new Date());
    }

    /**
     * 获取当前北京时间的毫秒值
     *
     * @return
     */
    public static long getUTC8Long() {
        //获取系统时间,该时间是通过GPS时间设置的
        long systime = System.currentTimeMillis();
        //将UTC0时区转为+8时区北京时间
        long utc8time = systime/* + 8*60*60*1000*/;
        return utc8time;
    }

    /**
     * 获取当前开机时间的毫秒值
     *
     * @return
     */
    public static long getSystemMilli() {
        //获取系统从开机到当前的纳秒值
        long nanoTime = System.nanoTime();
        //将UTC0时区转为+8时区北京时间
        long millitime = nanoTime / 1000000;
        return millitime;
    }

    /**
     * 获取当前北京时间的日期和时间
     *
     * @return YYYY-MM-dd|HH:mm:ss格式输出时间
     */
    public static String getUTC8DateTime() {
        long utc8time = getUTC8Long();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd|HH:mm:ss");
        return formatter.format(new Date(utc8time));
    }

    /**
     * 获取当前北京时间的日期和时间
     *
     * @return YYYY-MM-dd|HH:mm:ss格式输出时间
     */
    public static String getUTC8DateTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd|HH:mm:ss");
        return formatter.format(new Date(time));
    }

    /**
     * 获取当前北京时间的日期和时间
     *
     * @return YYYY-MM-dd-HH-mm-ss格式输出时间
     */
    public static String getUTC8DateTime_FileName() {
        long utc8time = getUTC8Long();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return formatter.format(new Date(utc8time));
    }

    /**
     * 获取当前时间戳
     *
     * @return
     */
    public static String getTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(new Date());
    }

    /**
     * 获取当前日期
     *
     * @return YYYY-MM-dd 格式输出时间
     */
    public static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(new Date());
    }

    /**
     * 获取某一个日期的年月日并保存到数组中
     *
     * @return 保存年月日信息的数组
     */
    public static int[] getCurDateBytes(String date) {
        int[] curDate = {2000, 1, 1};
        String[] array = date.split("-");
        if (array != null && array.length == 3) {
            curDate[0] = ConvertUtils.stringToInt(array[0]);
            curDate[1] = ConvertUtils.stringToInt(array[1]);
            curDate[2] = ConvertUtils.stringToInt(array[2]);
            return curDate;
        }
        return curDate;
    }

    /**
     * 获取日期时间字节数组
     *
     * @param time
     * @return
     */
    public static int[] getDateTimeBytes(String time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        int[] result = new int[6];
        result[0] = date.getYear() + 1900;
        result[1] = date.getMonth() + 1;
        result[2] = date.getDate();
        result[3] = date.getHours();
        result[4] = date.getMinutes();
        result[5] = date.getSeconds();
        return result;
    }

    /**
     * 获取打卡时间
     *
     * @return HH:mm:ss格式输出时间
     */
    public static String getDTime() {
        String datetime = getDateTime();
        return datetime.substring(11);
    }

    /**
     * 获取时间
     *
     * @return HH:mm格式输出时间
     */
    public static String getFTime() {
        String datetime = getDateTime();
        return datetime.substring(11, 16);
    }

    /**
     * 获取格式化的日期时间
     *
     * @param date
     * @param format
     * @return
     */
    public static String getFormatTime(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    /**
     * 根据日期类型获取日期字符串
     *
     * @param year
     * @param month
     * @param day
     */
    public static String getDateStr(int dateType, int year, int month, int day) {
        if (dateType == 0) {
            return String.format("%04d-%02d-%02d", year, month, day);
        } else if (dateType == 1) {
            return String.format("%04d-%02d", year, month);
        } else {
            return String.format("%02d-%02d", month, day);

        }
    }

    /**
     * 获取当前星期 中文
     *
     * @return string 星期一，星期二等
     */
    public static String getWeekdayChineseName(int weekday) {
        String result = null;
        switch (weekday) {
            case 0:
                result = "星期日";
                break;
            case 7:
                result = "星期日";
                break;
            case 1:
                result = "星期一";
                break;
            case 2:
                result = "星期二";
                break;
            case 3:
                result = "星期三";
                break;
            case 4:
                result = "星期四";
                break;
            case 5:
                result = "星期五";
                break;
            case 6:
                result = "星期六";
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 判断当前日期是星期几
     *
     * @param pTime 设置的需要判断的时间 //格式如2012-09-08
     * @return dayForWeek 判断结果
     * @Exception 发生异常
     */
    public static int getWeek(String pTime) {
        int Week = 0;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            Week = 0;
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week = 1;
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week = 2;
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week = 3;
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week = 4;
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week = 5;
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week = 6;
        }
        return Week;
    }

    /**
     * 获取指定日期 当前星期 中文
     *
     * @return string 星期一，星期二等
     */
    public static String getWeekdayChineseName(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date dateObj;
        try {
            dateObj = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return getWeekdayChineseName(dateObj.getDay());
    }

    /**
     * 获取毫秒级时间 转化为十六进制字符串 补上成十六位 通常用于拍照本地生成的ID
     *
     * @return
     */
    public static String getMilliTime() {
        String str = Long.toHexString(System.currentTimeMillis());
        StringBuffer sb = new StringBuffer();
        if (str.length() < 16) {
            for (int i = 0; i < 16 - str.length(); i++) {
                sb.append("0");
            }
        }
        sb.append(str);
        return sb.toString();
    }

    /**
     * 获取前一天日期
     *
     * @return YYYY-MM-dd 格式输出时间
     */
    public static String getLastDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String str = new String();
        date.setDate(date.getDate() - 1);
        str = formatter.format(date);
        return str;
    }

    /**
     * 获取time日期接下来days天后的日期,数组形式返回
     *
     * @return 0:year,1:month,2:day
     */
    public static int[] getNextDate(String time, int days) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date.setDate(date.getDate() + days);
        int[] result = new int[3];
        result[0] = date.getYear() + 1900;
        result[1] = (date.getMonth() + 1);
        result[2] = date.getDate();
        return result;
    }

    /**
     * 获取time日期接下来days天后的日期,字符串形式返回
     *
     * @return
     */
    public static String getNextDateString(String time, int days) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date.setDate(date.getDate() + days);
        return formatter.format(date);
    }

    /**
     * 开始时间是否早于结束时间
     *
     * @param starttime
     * @param endtime
     * @return true:是 false: 否
     */
    public static boolean isStartDateTimeBeforeEndDateTime(String starttime,
                                                           String endtime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date startdate = new Date();
        Date enddate = new Date();
        try {
            startdate = formatter.parse(starttime);
            enddate = formatter.parse(endtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (startdate.compareTo(enddate) < 0) {
            return true;
        }
        return false;
    }

    /**
     * 开始日期是否早于或等于结束日期
     *
     * @return true:是 false: 否
     */
    public static boolean isStartDateBeforeEndDate(String starttime,
                                                   String endtime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date startdate = new Date();
        Date enddate = new Date();
        try {
            startdate = formatter.parse(starttime);
            enddate = formatter.parse(endtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (startdate.compareTo(enddate) <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 开始时间是否早于或等于结束时间
     *
     * @return true:是 false: 否
     */
    public static boolean isStartTimeBeforeEndTime(String starttime,
                                                   String endtime) {
        SimpleDateFormat formatter = null;
        if (starttime.length() > 5) {
            formatter = new SimpleDateFormat("HH:mm:ss");
        } else {
            formatter = new SimpleDateFormat("HH:mm");
        }
        Date startdate = new Date();
        Date enddate = new Date();
        try {
            startdate = formatter.parse(starttime);
            enddate = formatter.parse(endtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (startdate.compareTo(enddate) <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 解析日期
     * @param dateStr 日期字符串
     * @param pattern 日期格式
     * @return Date时间
     */
    public static Date parse(String dateStr, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 解析日期
     * @param dateStr 日期字符串
     * @param pattern 日期格式
     * @return 时间戳
     */
    public static Long parseTimestamp(String dateStr, String pattern) {
        Date date = parse(dateStr, pattern);
        return date.getTime();
    }

    /**
     * 格式化日期
     * @param date date日期
     * @param pattern 日期格式
     * @return 日期字符串
     */
    public static String format(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern);
    }

    private static void validateDateNotNull(Date date) {
        Validate.isTrue(date != null, "The date must not be null", new Object[0]);
    }

    public static Date addYears(Date date, int amount) {
        return add(date, 1, amount);
    }

    public static Date addMonths(Date date, int amount) {
        return add(date, 2, amount);
    }

    public static Date addWeeks(Date date, int amount) {
        return add(date, 3, amount);
    }

    public static Date addDays(Date date, int amount) {
        return add(date, 5, amount);
    }

    public static Date addHours(Date date, int amount) {
        return add(date, 11, amount);
    }

    public static Date addMinutes(Date date, int amount) {
        return add(date, 12, amount);
    }

    public static Date addSeconds(Date date, int amount) {
        return add(date, 13, amount);
    }

    public static Date addMilliseconds(Date date, int amount) {
        return add(date, 14, amount);
    }
    private static Date add(Date date, int calendarField, int amount) {
        validateDateNotNull(date);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * 返回当前时间day天之后（day>0）或day天之前（day<0）的时间
     * @param date 日期
     * @param day
     * @return
     */
    public static Date addDay(Date date, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    /**
     * 返回当前时间month个月之后（month>0）或month个月之前（month<0）的时间
     *
     * @param date
     * @param month
     * @return
     */
    public static Date addMonth(Date date, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    /**
     * 返回当前时间year年之后（year>0）或year年之前（year<0）的时间
     *
     * @param date
     * @param year
     * @return
     */
    public static Date addYear(Date date, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * 获取日期
     * @param date
     * @return
     */
    public static Date getDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取星期
     * @param date
     * @return
     */
    public static int getWeek(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 得到年
     *
     * @param date Date对象
     * @return 年
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /**
     * 得到月
     *
     * @param date Date对象
     * @return 月
     */
    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH) + 1;

    }

    /**
     * 得到日
     *
     * @param date Date对象
     * @return 日
     */
    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /** Get millisecond of beginning of today. */
    public static long beginOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,  0);
        calendar.set(Calendar.MINUTE,       0);
        calendar.set(Calendar.SECOND,       0);
        calendar.set(Calendar.MILLISECOND,  0);
        return calendar.getTimeInMillis();
    }

    /** Get millisecond of beginning of this week. */
    public static long beginOfWeek(boolean sundayFirst) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,  0);
        calendar.set(Calendar.MINUTE,       0);
        calendar.set(Calendar.SECOND,       0);
        calendar.set(Calendar.MILLISECOND,  0);
        long millis = calendar.getTimeInMillis();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (sundayFirst) {
            millis -= (dayOfWeek-1) * DAY;
        } else {
            millis -= (dayOfWeek == Calendar.SUNDAY ? 6 : dayOfWeek-2) * DAY;
        }
        return millis;
    }

    /** Get millisecond of beginning of this month. */
    public static long beginOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE,         1);
        calendar.set(Calendar.HOUR_OF_DAY,  0);
        calendar.set(Calendar.MINUTE,       0);
        calendar.set(Calendar.SECOND,       0);
        calendar.set(Calendar.MILLISECOND,  0);
        return calendar.getTimeInMillis();
    }

    /** Get millisecond of beginning of this year. */
    public static long beginOfYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,        Calendar.JANUARY);
        calendar.set(Calendar.DATE,         1);
        calendar.set(Calendar.HOUR_OF_DAY,  0);
        calendar.set(Calendar.MINUTE,       0);
        calendar.set(Calendar.SECOND,       0);
        calendar.set(Calendar.MILLISECOND,  0);
        return calendar.getTimeInMillis();
    }

    /** Get millisecond of end of today. */
    public static long endOfToday() {
        return beginOfToday() + DAY - 1;
    }

    /** Get millisecond of end of this week. */
    public static long endOfWeek(boolean sundayFirst) {
        return beginOfWeek(sundayFirst) + 7*DAY - 1;
    }

    /** Get millisecond of end of this month. */
    public static long endOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,        calendar.get(Calendar.MONTH)+1);
        calendar.set(Calendar.DATE,         1);
        calendar.set(Calendar.HOUR_OF_DAY,  0);
        calendar.set(Calendar.MINUTE,       0);
        calendar.set(Calendar.SECOND,       0);
        calendar.set(Calendar.MILLISECOND,  0);
        return calendar.getTimeInMillis()-1;
    }

    /** Get millisecond of end of this year. */
    public static long endOfYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,         calendar.get(Calendar.YEAR)+1);
        calendar.set(Calendar.MONTH,        Calendar.JANUARY);
        calendar.set(Calendar.DATE,         1);
        calendar.set(Calendar.HOUR_OF_DAY,  0);
        calendar.set(Calendar.MINUTE,       0);
        calendar.set(Calendar.SECOND,       0);
        calendar.set(Calendar.MILLISECOND,  0);
        return calendar.getTimeInMillis()-1;
    }

    /**
     * 计算两个日期之间的天数差
     * @param start
     * @param end
     * @return
     */
    public static long daysBetween(Date start, Date end){
        if(start == null || end == null){
            logger.error("The date must not be null. start:" + start + ", end:" + end);
            return 0;
        }
        long endTime = end.getTime();
        long startTime = start.getTime();
        return (endTime - startTime) / (DAY);
    }

    /**
     * 转换日期 将日期转为今天, 昨天, 前天, XXXX-XX-XX, ...
     *
     * @param time 时间
     * @return 当前日期转换为更容易理解的方式
     */
    public static String translateDate(Long time) {
        long oneDay = 24 * 60 * 60 * 1000;
        Calendar current = Calendar.getInstance();
        //今天
        Calendar today = Calendar.getInstance();

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        long todayStartTime = today.getTimeInMillis();

        if (time >= todayStartTime && time < todayStartTime + oneDay) { // today
            return "今天";
        } else if (time >= todayStartTime - oneDay && time < todayStartTime) { // yesterday
            return "昨天";
        } else if (time >= todayStartTime - oneDay * 2 && time < todayStartTime - oneDay) { // the day before yesterday
            return "前天";
        } else if (time > todayStartTime + oneDay) { // future
            return "将来某一天";
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date(time);
            return dateFormat.format(date);
        }
    }

    /**
     * 转换日期 转换为更为人性化的时间
     *
     * @param time 时间
     * @return
     */
    private String translateDate(long time, long curTime) {
        long oneDay = 24 * 60 * 60;
        //今天
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(curTime * 1000);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        long todayStartTime = today.getTimeInMillis() / 1000;
        if (time >= todayStartTime) {
            long d = curTime - time;
            if (d <= 60) {
                return "1分钟前";
            } else if (d <= 60 * 60) {
                long m = d / 60;
                if (m <= 0) {
                    m = 1;
                }
                return m + "分钟前";
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("今天 HH:mm");
                Date date = new Date(time * 1000);
                String dateStr = dateFormat.format(date);
                if (!StringUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
                    dateStr = dateStr.replace(" 0", " ");
                }
                return dateStr;
            }
        } else {
            if (time < todayStartTime && time > todayStartTime - oneDay) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("昨天 HH:mm");
                Date date = new Date(time * 1000);
                String dateStr = dateFormat.format(date);
                if (!StringUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {

                    dateStr = dateStr.replace(" 0", " ");
                }
                return dateStr;
            } else if (time < todayStartTime - oneDay && time > todayStartTime - 2 * oneDay) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("前天 HH:mm");
                Date date = new Date(time * 1000);
                String dateStr = dateFormat.format(date);
                if (!StringUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
                    dateStr = dateStr.replace(" 0", " ");
                }
                return dateStr;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = new Date(time * 1000);
                String dateStr = dateFormat.format(date);
                if (!StringUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
                    dateStr = dateStr.replace(" 0", " ");
                }
                return dateStr;
            }
        }
    }
    public static String toString(final long millis) {
        return toString(millis, getDefaultFormat());
    }

    public static String toString(final long millis, final DateFormat format) {
        return format.format(new Date(millis));
    }

    public static long toMillis(final String time) {
        return toMillis(time, getDefaultFormat());
    }

    public static long toMillis(final String time, final DateFormat format) {
        try {
            return format.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Date toDate(final String time) {
        return toDate(time, getDefaultFormat());
    }

    public static Date toDate(final String time, final DateFormat format) {
        try {
            return format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toString(final Date date) {
        return toString(date, getDefaultFormat());
    }

    public static String toString(final Date date, final DateFormat format) {
        return format.format(date);
    }

    public static long toMillis(final Date date) {
        return date.getTime();
    }

    public static Date toDate(final long millis) {
        return new Date(millis);
    }

    // region *---------------------------------- 跨度 --------------------------------------*

    public static long span(final String time1,
                            final String time2,
                            final TimeConstants unit) {
        return span(time1, time2, getDefaultFormat(), unit);
    }

    public static long span(final String time1,
                            final String time2,
                            final DateFormat format,
                            final TimeConstants unit) {
        return toTimeSpan(toMillis(time1, format) - toMillis(time2, format), unit);
    }

    public static long span(final Date date1,
                            final Date date2,
                            final TimeConstants unit) {
        return toTimeSpan(toMillis(date1) - toMillis(date2), unit);
    }

    public static long span(final long millis1,
                            final long millis2,
                            final TimeConstants unit) {
        return toTimeSpan(millis1 - millis2, unit);
    }

    // endregion

    // region *---------------------------------- 判断 --------------------------------------*

    /**
     * Return whether it is today.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time The formatted time string.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isToday(final String time) {
        return isToday(toMillis(time, getDefaultFormat()));
    }

    /**
     * Return whether it is today.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isToday(final String time, final DateFormat format) {
        return isToday(toMillis(time, format));
    }

    /**
     * Return whether it is today.
     *
     * @param date The date.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isToday(final Date date) {
        return isToday(date.getTime());
    }

    /**
     * Return whether it is today.
     *
     * @param millis The milliseconds.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isToday(final long millis) {
        long wee = getWeeOfToday();
        return millis >= wee && millis < wee + TimeConstants.DAY.val;
    }

    public static boolean isLeapYear(final String time) {
        return isLeapYear(toDate(time, getDefaultFormat()));
    }

    public static boolean isLeapYear(final String time, final DateFormat format) {
        return isLeapYear(toDate(time, format));
    }

    public static boolean isLeapYear(final Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        return isLeapYear(year);
    }

    public static boolean isLeapYear(final long millis) {
        return isLeapYear(toDate(millis));
    }

    public static boolean isLeapYear(final int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    /**
     * Return whether it is am.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAm() {
        Calendar cal = Calendar.getInstance();
        return cal.get(GregorianCalendar.AM_PM) == 0;
    }

    /**
     * Return whether it is am.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time The formatted time string.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAm(final String time) {
        return getValueByCalendarField(time, getDefaultFormat(), GregorianCalendar.AM_PM) == 0;
    }

    /**
     * Return whether it is am.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAm(final String time,
                               final DateFormat format) {
        return getValueByCalendarField(time, format, GregorianCalendar.AM_PM) == 0;
    }

    /**
     * Return whether it is am.
     *
     * @param date The date.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAm(final Date date) {
        return getValueByCalendarField(date, GregorianCalendar.AM_PM) == 0;
    }

    /**
     * Return whether it is am.
     *
     * @param millis The milliseconds.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAm(final long millis) {
        return getValueByCalendarField(millis, GregorianCalendar.AM_PM) == 0;
    }

    /**
     * Return whether it is am.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPm() {
        return !isAm();
    }

    /**
     * Return whether it is am.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time The formatted time string.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPm(final String time) {
        return !isAm(time);
    }

    /**
     * Return whether it is am.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPm(final String time,
                               final DateFormat format) {
        return !isAm(time, format);
    }

    /**
     * Return whether it is am.
     *
     * @param date The date.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPm(final Date date) {
        return !isAm(date);
    }

    /**
     * Return whether it is am.
     *
     * @param millis The milliseconds.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPm(final long millis) {
        return !isAm(millis);
    }

    // endregion

    // region *------------------------------- 属相｜星座 ------------------------------------*

    /**
     * Return the Chinese zodiac.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time The formatted time string.
     * @return the Chinese zodiac
     */
    public static String getChineseZodiac(final String time) {
        return getChineseZodiac(toDate(time, getDefaultFormat()));
    }

    /**
     * Return the Chinese zodiac.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return the Chinese zodiac
     */
    public static String getChineseZodiac(final String time,final DateFormat format) {
        return getChineseZodiac(toDate(time, format));
    }

    /**
     * Return the Chinese zodiac.
     *
     * @param date The date.
     * @return the Chinese zodiac
     */
    public static String getChineseZodiac(final Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return CHINESE_ZODIAC[cal.get(Calendar.YEAR) % 12];
    }

    /**
     * Return the Chinese zodiac.
     *
     * @param millis The milliseconds.
     * @return the Chinese zodiac
     */
    public static String getChineseZodiac(final long millis) {
        return getChineseZodiac(toDate(millis));
    }

    /**
     * Return the Chinese zodiac.
     *
     * @param year The year.
     * @return the Chinese zodiac
     */
    public static String getChineseZodiac(final int year) {
        return CHINESE_ZODIAC[year % 12];
    }

    /**
     * Return the zodiac.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time The formatted time string.
     * @return the zodiac
     */
    public static String getZodiac(final String time) {
        return getZodiac(toDate(time, getDefaultFormat()));
    }

    /**
     * Return the zodiac.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return the zodiac
     */
    public static String getZodiac(final String time, final DateFormat format) {
        return getZodiac(toDate(time, format));
    }

    /**
     * Return the zodiac.
     *
     * @param date The date.
     * @return the zodiac
     */
    public static String getZodiac(final Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return getZodiac(month, day);
    }

    /**
     * Return the zodiac.
     *
     * @param millis The milliseconds.
     * @return the zodiac
     */
    public static String getZodiac(final long millis) {
        return getZodiac(toDate(millis));
    }

    /**
     * Return the zodiac.
     *
     * @param month The month.
     * @param day   The day.
     * @return the zodiac
     */
    public static String getZodiac(final int month, final int day) {
        return ZODIAC[day >= ZODIAC_FLAGS[month - 1]
                ? month - 1
                : (month + 10) % 12];
    }

    // endregion

    // region *------------------------------- Calendar ------------------------------------*

    /**
     * Returns the value of the given calendar field.
     *
     * @param field The given calendar field.
     *              <ul>
     *              <li>{@link Calendar#ERA}</li>
     *              <li>{@link Calendar#YEAR}</li>
     *              <li>{@link Calendar#MONTH}</li>
     *              <li>...</li>
     *              <li>{@link Calendar#DST_OFFSET}</li>
     *              </ul>
     * @return the value of the given calendar field
     */
    public static int getValueByCalendarField(final int field) {
        Calendar cal = Calendar.getInstance();
        return cal.get(field);
    }

    /**
     * Returns the value of the given calendar field.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time  The formatted time string.
     * @param field The given calendar field.
     *              <ul>
     *              <li>{@link Calendar#ERA}</li>
     *              <li>{@link Calendar#YEAR}</li>
     *              <li>{@link Calendar#MONTH}</li>
     *              <li>...</li>
     *              <li>{@link Calendar#DST_OFFSET}</li>
     *              </ul>
     * @return the value of the given calendar field
     */
    public static int getValueByCalendarField(final String time, final int field) {
        return getValueByCalendarField(toDate(time, getDefaultFormat()), field);
    }

    /**
     * Returns the value of the given calendar field.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @param field  The given calendar field.
     *               <ul>
     *               <li>{@link Calendar#ERA}</li>
     *               <li>{@link Calendar#YEAR}</li>
     *               <li>{@link Calendar#MONTH}</li>
     *               <li>...</li>
     *               <li>{@link Calendar#DST_OFFSET}</li>
     *               </ul>
     * @return the value of the given calendar field
     */
    public static int getValueByCalendarField(final String time,
                                              final DateFormat format,
                                              final int field) {
        return getValueByCalendarField(toDate(time, format), field);
    }

    /**
     * Returns the value of the given calendar field.
     *
     * @param date  The date.
     * @param field The given calendar field.
     *              <ul>
     *              <li>{@link Calendar#ERA}</li>
     *              <li>{@link Calendar#YEAR}</li>
     *              <li>{@link Calendar#MONTH}</li>
     *              <li>...</li>
     *              <li>{@link Calendar#DST_OFFSET}</li>
     *              </ul>
     * @return the value of the given calendar field
     */
    public static int getValueByCalendarField(final Date date, final int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(field);
    }

    /**
     * Returns the value of the given calendar field.
     *
     * @param millis The milliseconds.
     * @param field  The given calendar field.
     *               <ul>
     *               <li>{@link Calendar#ERA}</li>
     *               <li>{@link Calendar#YEAR}</li>
     *               <li>{@link Calendar#MONTH}</li>
     *               <li>...</li>
     *               <li>{@link Calendar#DST_OFFSET}</li>
     *               </ul>
     * @return the value of the given calendar field
     */
    public static int getValueByCalendarField(final long millis, final int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return cal.get(field);
    }

    // endregion

    // region *---------------------------------- 内部 --------------------------------------*

    private static final ThreadLocal<SimpleDateFormat> SDF_THREAD_LOCAL = new ThreadLocal<>();

    private static SimpleDateFormat getDefaultFormat() {
        SimpleDateFormat simpleDateFormat = SDF_THREAD_LOCAL.get();
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SDF_THREAD_LOCAL.set(simpleDateFormat);
        }
        return simpleDateFormat;
    }

    private static long getWeeOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * TODO 下面只能算是一种计算方式，即指定时间跨度中天的个数，但是：
     * 1. 不到一天怎么算？
     * 2. 起止日期怎么算？
     */
    private static long toTimeSpan(final long millis, TimeConstants unit) {
        return millis / unit.val;
    }


}
