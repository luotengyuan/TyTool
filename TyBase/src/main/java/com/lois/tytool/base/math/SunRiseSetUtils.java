package com.lois.tytool.base.math;

/**
 * Description: 日出日落计算工具类
 * User: Luo.T.Y
 * Date: 2018-02-06
 * Time: 16:58
 */
public class SunRiseSetUtils {
    private static final String TAG = SunRiseSetUtils.class.getSimpleName();
    private static int[] days_of_month_1 = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static int[] days_of_month_2 = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private final static double h = -0.833;//日出日落时太阳的位置
    private final static double UTo = 180.0;//上次计算的日落日出时间，初始迭代值180.0

    /**
     * 判断是否为闰年
     * @param year 输入年份
     * @return 若为闰年，返回true；若不是闰年,返回false
     */
    private static boolean leap_year(int year) {
        if (((year % 400 == 0) || (year % 100 != 0) && (year % 4 == 0))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 求从格林威治时间公元2000年1月1日到计算日天数days
     * @param year
     * @param month
     * @param date
     * @return
     */
    private static int days(int year, int month, int date) {
        int i, a = 0;
        for (i = 2000; i < year; i++) {
            if (leap_year(i)) {
                a = a + 366;
            } else {
                a = a + 365;
            }
        }
        if (leap_year(year)) {
            for (i = 0; i < month - 1; i++) {
                a = a + days_of_month_2[i];
            }
        } else {
            for (i = 0; i < month - 1; i++) {
                a = a + days_of_month_1[i];
            }
        }
        a = a + date;
        return a;
    }

    /**
     * 求格林威治时间公元2000年1月1日到计算日的世纪数t
     * @param days
     * @param UTo
     * @return
     */
    private static double t_century(int days, double UTo) {
        return ((double) days + UTo / 360) / 36525;
    }

    /**
     * 求太阳的平黄径
     * @param t_century
     * @return
     */
    private static double L_sun(double t_century) {
        return (280.460 + 36000.770 * t_century);
    }

    /**
     * 求太阳的平近点角
     * @param t_century
     * @return
     */
    private static double G_sun(double t_century) {
        return (357.528 + 35999.050 * t_century);
    }

    /**
     * 求黄道经度
     * @param L_sun
     * @param G_sun
     * @return
     */
    private static double ecliptic_longitude(double L_sun, double G_sun) {
        return (L_sun + 1.915 * Math.sin(G_sun * Math.PI / 180) + 0.02 * Math.sin(2 * G_sun * Math.PI / 180));
    }

    /**
     * 求地球倾角
     * @param t_century
     * @return
     */
    private static double earth_tilt(double t_century) {
        return (23.4393 - 0.0130 * t_century);
    }

    /**
     * 求太阳偏差
     * @param earth_tilt
     * @param ecliptic_longitude
     * @return
     */
    private static double sun_deviation(double earth_tilt, double ecliptic_longitude) {
        return (180 / Math.PI * Math.asin(Math.sin(Math.PI / 180 * earth_tilt) * Math.sin(Math.PI / 180 * ecliptic_longitude)));
    }

    /**
     * 求格林威治时间的太阳时间角GHA
     * @param UTo
     * @param G_sun
     * @param ecliptic_longitude
     * @return
     */
    private static double GHA(double UTo, double G_sun, double ecliptic_longitude) {
        return (UTo - 180 - 1.915 * Math.sin(G_sun * Math.PI / 180) - 0.02 * Math.sin(2 * G_sun * Math.PI / 180) + 2.466 * Math.sin(2 * ecliptic_longitude * Math.PI / 180) - 0.053 * Math.sin(4 * ecliptic_longitude * Math.PI / 180));
    }

    /**
     * 求修正值e
     * @param h
     * @param glat
     * @param sun_deviation
     * @return
     */
    private static double e(double h, double glat, double sun_deviation) {
        return 180 / Math.PI * Math.acos((Math.sin(h * Math.PI / 180) - Math.sin(glat * Math.PI / 180) * Math.sin(sun_deviation * Math.PI / 180)) / (Math.cos(glat * Math.PI / 180) * Math.cos(sun_deviation * Math.PI / 180)));
    }

    /**
     * 求日出时间
     * @param UTo
     * @param GHA
     * @param glong
     * @param e
     * @return
     */
    private static double UT_rise(double UTo, double GHA, double glong, double e) {
        return (UTo - (GHA + glong + e));
    }

    /**
     * 求日落时间
     * @param UTo
     * @param GHA
     * @param glong
     * @param e
     * @return
     */
    private static double UT_set(double UTo, double GHA, double glong, double e) {
        return (UTo - (GHA + glong - e));
    }

    /**
     * 判断并返回结果（日出），计算以度为单位,即180°=12小时,因此需要转化为以小时表示的时间,再加上所在的时区数Zone,即要计算地的日出日落时间为 ：T=UT/15+Zone
     *
     * @param UT
     * @param UTo
     * @param glong
     * @param glat
     * @param year
     * @param month
     * @param date
     * @return
     */
    private static double result_rise(double UT, double UTo, double glong, double glat, int year, int month, int date) {
        double d;
        if (UT >= UTo) {
            d = UT - UTo;
        } else {
            d = UTo - UT;
        }
        if (d >= 0.1) {
            UTo = UT;
            UT = UT_rise(UTo,
                    GHA(UTo, G_sun(t_century(days(year, month, date), UTo)),
                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),
                                    G_sun(t_century(days(year, month, date), UTo)))),
                    glong,
                    e(h, glat, sun_deviation(earth_tilt(t_century(days(year, month, date), UTo)),
                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),
                                    G_sun(t_century(days(year, month, date), UTo))))));
            result_rise(UT, UTo, glong, glat, year, month, date);

        }
        return UT;
    }

    /**
     * 判断并返回结果（日落），计算以度为单位,即180°=12小时,因此需要转化为以小时表示的时间,再加上所在的时区数Zone,即要计算地的日出日落时间为 ：T=UT/15+Zone
     *
     * @param UT
     * @param UTo
     * @param glong
     * @param glat
     * @param year
     * @param month
     * @param date
     * @return
     */
    private static double result_set(double UT, double UTo, double glong, double glat, int year, int month, int date) {
        double d;
        if (UT >= UTo) {
            d = UT - UTo;
        } else {
            d = UTo - UT;
        }
        if (d >= 0.1) {
            UTo = UT;
            UT = UT_set(UTo,
                    GHA(UTo, G_sun(t_century(days(year, month, date), UTo)),
                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),
                                    G_sun(t_century(days(year, month, date), UTo)))),
                    glong,
                    e(h, glat, sun_deviation(earth_tilt(t_century(days(year, month, date), UTo)),
                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),
                                    G_sun(t_century(days(year, month, date), UTo))))));
            result_set(UT, UTo, glong, glat, year, month, date);
        }
        return UT;
    }

    /**
     * 求时区
     * @param glong
     * @return
     */
    private static int Zone(double glong) {
        if (glong >= 0) {
            return (int) ((int) (glong / 15.0) + 1);
        } else {
            return (int) ((int) (glong / 15.0) - 1);
        }
    }

    /**
     * 计算某个位置的日出时间
     *
     * @param lon     经度
     * @param lat     纬度
     * @param year    年份
     * @param month   月份
     * @param day     日期
     * @param inChina 如果在中国时区使用东8区
     * @return 返回日出在当天的时间，1.5 --> 1:30  3.75 --> 3:45
     */
    public static double getRiseTime(double lon, double lat, int year, int month, int day, boolean inChina) {
        int timeZone;
        if (inChina) {
            timeZone = 8;
        } else {
            timeZone = Zone(lon);
        }
        double sunrise = result_rise(UT_rise(UTo,
                GHA(UTo, G_sun(t_century(days(year, month, day), UTo)),
                        ecliptic_longitude(L_sun(t_century(days(year, month, day), UTo)),
                                G_sun(t_century(days(year, month, day), UTo)))),
                lon,
                e(h, lat, sun_deviation(earth_tilt(t_century(days(year, month, day), UTo)),
                        ecliptic_longitude(L_sun(t_century(days(year, month, day), UTo)),
                                G_sun(t_century(days(year, month, day), UTo)))))), UTo, lon, lat, year, month, day);
        return sunrise / 15 + timeZone;
    }

    /**
     * 计算某个位置的日落时间
     *
     * @param lon     经度
     * @param lat     纬度
     * @param year    年份
     * @param month   月份
     * @param day     日期
     * @param inChina 如果在中国时区使用东8区
     * @return 返回日出在当天的时间，1.5 --> 1:30  3.75 --> 3:45
     */
    public static double getSetTime(double lon, double lat, int year, int month, int day, boolean inChina) {
        int timeZone;
        if (inChina) {
            timeZone = 8;
        } else {
            timeZone = Zone(lon);
        }
        double sunset = result_set(UT_set(UTo,
                GHA(UTo, G_sun(t_century(days(year, month, day), UTo)),
                        ecliptic_longitude(L_sun(t_century(days(year, month, day), UTo)),
                                G_sun(t_century(days(year, month, day), UTo)))),
                lon,
                e(h, lat, sun_deviation(earth_tilt(t_century(days(year, month, day), UTo)),
                        ecliptic_longitude(L_sun(t_century(days(year, month, day), UTo)),
                                G_sun(t_century(days(year, month, day), UTo)))))), UTo, lon, lat, year, month, day);
        return sunset / 15 + timeZone;
    }
}
