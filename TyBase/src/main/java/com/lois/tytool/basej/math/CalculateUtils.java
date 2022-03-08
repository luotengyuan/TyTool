package com.lois.tytool.basej.math;

import com.lois.tytool.basej.gis.LonLat;

/**
 * @Description 计算相关
 * @Author Luo.T.Y
 * @Date 2017-09-22
 * @Time 14:42
 */
public class CalculateUtils {

    /**
     * 地球半径 单位:千米
     */
    public static final double EARTH_RADIUS = 6378.137;

    /**
     * 获取数据区带进位累加校验码
     *
     * @param byteData 数据
     * @return单字节带进位累加校验码
     */
    public static byte calCheckSumByte(byte[] byteData) {
        short result = 0;
        int len = 0;
        if (byteData == null || byteData.length == 0) {
            return 0;
        }
        len = byteData.length;
        for (int i = 0; i < len; i++) {
            result += ConvertUtils.byteToInt(byteData[i]);
            if ((result & 0xff00) >> 8 != 0) {
                char low = (char) ((char) ((result & 0xff00) >> 8) + (char) (result & 0x00ff));
                result = (short) low;
            }
        }
        return (byte) (result & 0x00ff);
    }

    /**
     * 计算累加和 (使用时确保要计算累加和数据长度是byteData的长度)
     *
     * @param byteData 数据体
     * @return累加和
     */
    public static int calCheckSum(byte[] byteData) {
        return calCheckSum(byteData, byteData.length);
    }

    /**
     * 计算累加和
     *
     * @param byteData 数据体
     * @param len      数据长度
     * @return累加和
     */
    public static int calCheckSum(byte[] byteData, int len) {
        int chksum = 0;
        if (byteData == null || byteData.length < len) {
            return 0;
        }
        for (int i = 0; i < len; i++) {
            chksum += ConvertUtils.byteToInt(byteData[i]);
        }
        return chksum;
    }

    /**
     * 计算单个字节的补码，即: 如b>=0，则返回b；反之，则返回b的反码加1
     *
     * @param b 待计算的单字节数据
     * @return 补码
     */
    public static int calComplement(byte b) {
        return (b >= 0) ? b : (256 + b);
    }

    /**
     * 把角度值转换成弧度
     *
     * @param angle 角度值 单位:度
     * @return 弧度值 单位:弧度
     */
    public static double getRadValue(double angle) {
        return angle * Math.PI / 180.0;
    }

    /**
     * 根据两个位置的经纬度,来计算两地的距离 单位:米
     *
     * @param lon1 位置1经度
     * @param lat1 位置1纬度
     * @param lon2 位置2经度
     * @param lat2 位置2纬度
     * @return
     */
    public static double getDistance(double lon1, double lat1, double lon2, double lat2) {
        double a, b, d, sa2, sb2;
        lat1 = getRadValue(lat1);
        lat2 = getRadValue(lat2);
        a = lat1 - lat2;
        b = getRadValue(lon1 - lon2);

        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * EARTH_RADIUS * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
        return d * 1000;
    }

    /**
     * 根据两个位置的经纬度,来计算两地的距离(方法2) 单位:米
     *
     * @param lon1 位置1经度
     * @param lat1 位置1纬度
     * @param lon2 位置2经度
     * @param lat2 位置2纬度
     * @return 两点距离 单位:米
     */
    public static double getDistance2(double lon1, double lat1, double lon2, double lat2) {
        double x, y, distance;
        x = (lon2 - lon1) * Math.PI * EARTH_RADIUS * 1000 * Math.cos(((lat1 + lat2) / 2) * Math.PI / 180) / 180;
        y = (lat2 - lat1) * Math.PI * EARTH_RADIUS * 1000 / 180;
        distance = Math.hypot(x, y);
        return distance;
    }

    /**
     * 计算线段的矢量化方向(与正北方向的夹角)
     *
     * @param lonStart 线段起点经度 单位:度
     * @param latStart 线段起点纬度 单位:度
     * @param lonEnd   线段终点经度 单位:度
     * @param latEnd   线段终点纬度 单位:度
     * @return 矢量化方向 单位:度
     */
    public static double getDirection(double lonStart, double latStart, double lonEnd, double latEnd) {
        double FromEc = 6356725 + (6378137 - 6356725) * (90.0 - latStart) / 90.0;
        double FromEd = FromEc * Math.cos(latStart * Math.PI / 180.0);
        double dx = ((lonEnd * Math.PI / 180.0) - (lonStart * Math.PI / 180.0)) * FromEd;
        double dy = ((latEnd * Math.PI / 180.0) - (latStart * Math.PI / 180.0)) * FromEc;
        double angle = Math.atan(Math.abs(dx / dy)) * 180.0 / Math.PI;

        double dLon = lonEnd - lonStart;
        double dLat = latEnd - latStart;

        if (dLon > 0 && dLat <= 0) {
            angle = (90.0 - angle) + 90;
        } else if (dLon <= 0 && dLat < 0) {
            angle = angle + 180.0;
        } else if (dLon < 0 && dLat >= 0) {
            angle = (90.0 - angle) + 270;
        }

        return angle;
    }

    /**
     * 二维空间点到直线的垂足
     *
     * @param pt    直线外一点
     * @param begin 直线起始点
     * @param end   直线结束点
     * @return
     */
    public static LonLat getFootOfLonLatToLine(LonLat pt, LonLat begin, LonLat end) {

        double dx = begin.getLongitude() - end.getLongitude();
        double dy = begin.getLatitude() - end.getLatitude();
        if (Math.abs(dx) < 0.00000001 && Math.abs(dy) < 0.00000001) {
            return begin;
        }

        double u = (pt.getLongitude() - begin.getLongitude()) * (begin.getLongitude() - end.getLongitude())
                + (pt.getLatitude() - begin.getLatitude()) * (begin.getLatitude() - end.getLatitude());
        u = u / ((dx * dx) + (dy * dy));

        return new LonLat(begin.getLongitude() + u * dx, begin.getLatitude() + u * dy);
    }

    /**
     * 二维空间点到直线的垂足与端点的距离(在两端点内则返回0,在端点外则返回与最近一个端点的距离.)
     *
     * @param pt    直线外一点
     * @param begin 直线起始点
     * @param end   直线结束点
     * @return
     */
    public static double getFootDistanceOfLonLatToLine(LonLat pt, LonLat begin, LonLat end) {
        double angle = getAngleOfLine(begin, end);
        LonLat pt_ = rotateLonLat(pt, angle);
        LonLat begin_ = rotateLonLat(begin, angle);
        LonLat end_ = rotateLonLat(end, angle);
        double difBegin = pt_.getLongitude() - begin_.getLongitude();
        double difEnd = pt_.getLongitude() - end_.getLongitude();
        if (difBegin < 0 && difEnd < 0) {
            return Math.abs(difBegin) > Math.abs(difEnd) ? Math.abs(difEnd) : Math.abs(difBegin);
        } else if (difBegin > 0 && difEnd > 0) {
            return difBegin > difEnd ? difEnd : difBegin;
        } else {
            return 0;
        }
    }

    /**
     * 获取二维坐标点绕坐标原点顺时针旋转指定角度后的坐标位置
     *
     * @param p     旋转前坐标
     * @param angle 旋转角度
     * @return 旋转后坐标
     */
    public static LonLat rotateLonLat(LonLat p, double angle) {
        double lon = p.getLongitude() * Math.cos(angle * Math.PI / 180) + p.getLatitude() * Math.sin(angle * Math.PI / 180);
        double lat = p.getLatitude() * Math.cos(angle * Math.PI / 180) - p.getLongitude() * Math.sin(angle * Math.PI / 180);
        return new LonLat(lon, lat);
    }

    /**
     * 获取直线的夹角
     *
     * @param p1 直线上一点p1
     * @param p2 直线上一点p2
     * @return 直线夹角
     */
    public static double getAngleOfLine(LonLat p1, LonLat p2) {
        double angle = 0;
        if (p1.getLongitude() - p2.getLongitude() == 0) {
            angle = 90;
        } else if (p1.getLatitude() - p2.getLatitude() == 0) {
            angle = 0;
        } else {
            // 计算斜率
            double slope = (p1.getLatitude() - p2.getLatitude()) / (p1.getLongitude() - p2.getLongitude());
            // 计算反正切值
            double acrtan = Math.atan(slope);
            if (acrtan > 0) {
                angle = acrtan * 180 / Math.PI;
            } else {
                angle = 180 + acrtan * 180 / Math.PI;
            }
        }
        return angle;
    }

    /**
     * 二维空间点到直线的距离
     *
     * @param pt    直线外一点
     * @param begin 直线起始点
     * @param end   直线结束点
     * @return 点到直线的距离 单位:米
     */
    public static double getDistanceOfLonLatToLine(LonLat pt, LonLat begin, LonLat end) {
        double dis = 0;
        if (begin.getLongitude() == end.getLongitude()) {
            dis = Math.abs(pt.getLongitude() - begin.getLongitude());
            return dis;
        }
        double lineK = (end.getLatitude() - begin.getLatitude()) / (end.getLongitude() - begin.getLongitude());
        double lineC = (end.getLongitude() * begin.getLatitude() - begin.getLongitude() * end.getLatitude())
                / (end.getLongitude() - begin.getLongitude());
        dis = Math.abs(lineK * pt.getLongitude() - pt.getLatitude() + lineC) / (Math.sqrt(lineK * lineK + 1));
        return dis;
    }

    /**
     * 将距离单位米转成经纬度数差
     *
     * @param meter 缓冲区距离 单位:米
     * @return 度数差 单位:度
     */
    public static double meter2Deg(double meter) {
        // 1度等于111千米
        return meter * (1 / 111000.0);
    }

    /**
     * 将经度纬度距离近似转换成米
     *
     * @return
     */
    public static double deg2Meter(double deg) {
        // 1度等于111千米
        return deg * 111000.0;
    }

    /**
     * 获取点到直线的最小距离
     *
     * @return 点到直线的最小距离 单位:米
     */
    public static double getPointMinDistToLine(LonLat pt, LonLat begin, LonLat end) {
        double dist1 = getFootDistanceOfLonLatToLine(pt, begin, end);
        double dist2 = getDistanceOfLonLatToLine(pt, begin, end);
        return dist1 > dist2 ? deg2Meter(dist1) : deg2Meter(dist2);
    }
}
