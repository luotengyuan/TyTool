package com.lois.tytool.basej.gis;

/**
 * @Description 百度、火星、GPS坐标相互转换工具
 * @Author Luo.T.Y
 * @Date 2017-09-23
 * @Time 13:55
 */
public class CoorsTransform {
    private static final String TAG = CoorsTransform.class.getSimpleName();
    private static final  double pi = 3.1415926535897932384626;
    /**
     * 1975年国际椭球体长半轴
     */
    private static final  double a = 6378140.0;
    /**
     * 1975年国际椭球体扁率
     */
    private static final  double ee = 0.0033528131778969143;

    /**
     * 百度坐标系转换有0.001级别的误差，若对精度要求高，可采用百度官方接口
     * gps实体类可自行构造，其属性字段为经纬度。
     * @param lon
     * @param lat
     * @return
     */
    public static LonLat GPS84ToBD09(double lon, double lat) {
        if (outOfChina(lon, lat)) {
            return null;
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);
        double mgLon = lon + dLon;
        double mgLat = lat + dLat;
        double z = Math.sqrt(mgLon * mgLon + mgLat * mgLat) + 0.00002 * Math.sin(mgLat * Math.PI);
        double theta = Math.atan2(mgLat, mgLon) + 0.000003 * Math.cos(mgLon * Math.PI);
        double longitude = z * Math.cos(theta) + 0.0065;
        double latitude = z * Math.sin(theta) + 0.006;
        return new LonLat(longitude, latitude, CoordType.BD09);
    }

    public static LonLat GPS84ToGCJ02(double lon, double lat) {
        if (outOfChina(lon, lat)) {
            return null;
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new LonLat(mgLon, mgLat, CoordType.GCJ02);
    }

    public static LonLat GCJ02ToGPS84(double lon, double lat) {
        LonLat gps = transform(lon, lat);
        double lontitude = lon * 2 - gps.getLongitude();
        double latitude = lat * 2 - gps.getLatitude();
        return new LonLat(lontitude, latitude, CoordType.WGS84);
    }

    public static LonLat GCJ02ToBD09(double gg_lon, double gg_lat) {
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * pi);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new LonLat(bd_lon, bd_lat, CoordType.BD09);
    }

    public static LonLat BD09ToGCJ02(double bd_lon, double bd_lat) {
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new LonLat(gg_lon, gg_lat, CoordType.GCJ02);
    }

    public static LonLat BD09ToGPS84(double bd_lon, double bd_lat) {
        LonLat gcj02 = BD09ToGCJ02(bd_lon, bd_lat);
        LonLat map84 = GCJ02ToGPS84(gcj02.getLongitude(), gcj02.getLatitude());
        return map84;
    }

    private static boolean outOfChina(double lon, double lat) {
        if (lon < 72.004 || lon > 137.8347) {
            return true;
        }
        return lat < 0.8293 || lat > 55.8271;
    }

    private static LonLat transform(double lon, double lat) {
        if (outOfChina(lon, lat)) {
            return new LonLat(lon, lat);
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new LonLat(mgLon, mgLat, CoordType.UNKNOWN);
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }
}
