package com.lois.tytool.base.gis;

/**
 * @Description 纬度经度类
 * @Author Luo.T.Y
 * @Date 2017-09-22
 * @Time 15:11
 */
public class LonLat {
    /**
     * 纬度 单位:度
     */
    private double latitude;
    /**
     * 经度 单位:度
     */
    private double longitude;
    /**
     * 坐标系类型
     */
    private CoordType type;

    /**
     * 构造方法
     */
    public LonLat(double longitude, double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        type = CoordType.UNKNOWN;
    }

    /**
     * 构造方法
     */
    public LonLat(double longitude, double latitude, CoordType type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public CoordType getType() {
        return type;
    }

    public void setType(CoordType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "LonLat{" +
                "longitude=" +  longitude+
                ", latitude=" + latitude +
                ", type=" + type +
                '}';
    }
}
