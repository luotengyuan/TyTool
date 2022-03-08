package com.lois.tytool;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.lois.tytool.base.gis.CoordType;
import com.lois.tytool.base.gis.CoorsTransform;
import com.lois.tytool.base.gis.LonLat;
import com.lois.tytool.base.io.LogSaveUtils;
import com.lois.tytool.base.time.DateTimeUtils;
import com.lois.tytool.io.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @Description GPS相关工具类
 *
 * 开启、关闭GPS
 * 添加、移除监听
 * 开启、关闭位置记录
 * 开启、关闭NMEA记录
 * 全局的GPS信息
 * 默认地址信息获取与设置接口
 * 通用工具类
 * 
 * @Author Luo.T.Y
 * @Date 2022/2/18
 * @Time 20:28
 */
public class TyGps implements LocationListener, GpsStatus.NmeaListener{
    private static final String TAG = TyGps.class.getSimpleName();
    private LocationManager mLocationManager;

    private List<OnLocationChangeListener> mLocationChangeListeners;
    private List<OnStatusChangeListener> mStatusChangeListeners;
    private List<OnNmeaReceivedListener> mNmeaReceivedListeners;

    private boolean mIsStart = false;

    private Criteria mCriteria;
    private int mMinTime = 0;
    private int mMinDistance = 0;
    /**
     * 是否过滤模拟位置
     */
    private boolean mIsFilterMock = false;
    /**
     * 是否保存GPS信息到文件
     */
    private boolean mIsGpsToFile = false;
    /**
     * 保存GPS信息到文件名称
     */
    private String mGpsToFilePath = "";
    /**
     * 是否保存NMEA信息到文件
     */
    private boolean mIsNmeaToFile = false;
    /**
     * 保存Nmea信息到文件名称
     */
    private String mNmeaToFilePath = "";
    /**
     * 当前位置信息
     */
    private Location mCurLocation = null;
    /**
     * 地址解析器
     */
    private AddressParse mAddressParse = new GeocoderAddressParse();

    private TyGps() {
        mLocationChangeListeners = new ArrayList<>();
        mStatusChangeListeners = new ArrayList<>();
        mNmeaReceivedListeners = new ArrayList<>();
        mCriteria = getDefaultCriteria();
    }

    private static class SingletonInstance {
        private static final TyGps INSTANCE = new TyGps();
    }

    public static TyGps getInstance() {
        return SingletonInstance.INSTANCE;
    }

    /**
     * 开启GPS服务
     * @return true：成功  false：失败
     */
    public boolean start() {
        if (mIsStart) {
            return true;
        }
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) TyTool.getInstance().getContext().getSystemService(Context.LOCATION_SERVICE);
        }
        if (mLocationManager == null) {
            Log.w(TAG, "无法定位，获取定位服务失败");
            return false;
        }
        if (!isLocationEnabled()) {
            Log.w(TAG, "无法定位，请打开定位服务");
            return false;
        }
        String provider = mLocationManager.getBestProvider(mCriteria, true);
        if (ActivityCompat.checkSelfPermission(TyTool.getInstance().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TyTool.getInstance().getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "无法定位，请打开定位权限");
            return false;
        }
        Location location = mLocationManager.getLastKnownLocation(provider);
        if (location != null) {
            if (mLocationChangeListeners != null) {
                for (OnLocationChangeListener listener : mLocationChangeListeners) {
                    listener.getLastKnownLocation(location);
                }
            }
            mCurLocation = location;
        }
        mLocationManager.requestLocationUpdates(provider, mMinTime, mMinDistance, this);
        mLocationManager.addNmeaListener(this);
        mIsStart = true;
        return true;
    }

    /**
     * 停止GPS服务
     */
    public void stop() {
        removeAllListener();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
            mLocationManager.removeNmeaListener(this);
            mLocationManager = null;
        }
        mIsStart = false;
    }

    public Criteria getCriteria() {
        return mCriteria;
    }

    public TyGps setCriteria(Criteria criteria) {
        this.mCriteria = criteria;
        return this;
    }

    public int getMinTime() {
        return mMinTime;
    }

    public TyGps setMinTime(int minTime) {
        this.mMinTime = minTime;
        return this;
    }

    public int getMinDistance() {
        return mMinDistance;
    }

    public TyGps setMinDistance(int minDistance) {
        this.mMinDistance = minDistance;
        return this;
    }

    public boolean isFilterMock() {
        return mIsFilterMock;
    }

    public TyGps setFilterMock(boolean mIsFilterMock) {
        this.mIsFilterMock = mIsFilterMock;
        return this;
    }

    public boolean isGpsToFile() {
        return mIsGpsToFile;
    }

    public TyGps setGpsToFile(boolean mIsGpsToFile) {
        if (this.mIsGpsToFile != mIsGpsToFile) {
            this.mIsGpsToFile = mIsGpsToFile;
            if (mIsGpsToFile) {
                mGpsToFilePath = TyTool.getInstance().getAppDir() + "gps/" + DateTimeUtils.getDateTimeFileName() + "_gps.csv";
            }
        }
        return this;
    }

    public boolean isNmeaToFile() {
        return mIsNmeaToFile;
    }

    public TyGps setNmeaToFile(boolean mIsNmeaToFile) {
        if (this.mIsNmeaToFile != mIsNmeaToFile) {
            this.mIsNmeaToFile = mIsNmeaToFile;
            if (mIsNmeaToFile) {
                mNmeaToFilePath = TyTool.getInstance().getAppDir() + "gps/" + DateTimeUtils.getDateTimeFileName() + "_nmea.txt";
            }
        }
        return this;
    }

    public TyGps addLocationChangeListener(OnLocationChangeListener listener) {
        if (mLocationChangeListeners == null) {
            mLocationChangeListeners = new ArrayList<>();
        }
        if (!mLocationChangeListeners.contains(listener)) {
            mLocationChangeListeners.add(listener);
        }
        return this;
    }

    public TyGps removeLocationChangeListener(OnLocationChangeListener listener) {
        if (mLocationChangeListeners != null && mLocationChangeListeners.contains(listener)) {
            mLocationChangeListeners.remove(listener);
        }
        return this;
    }

    public TyGps addStatusChangeListener(OnStatusChangeListener listener) {
        if (mStatusChangeListeners == null) {
            mStatusChangeListeners = new ArrayList<>();
        }
        if (!mStatusChangeListeners.contains(listener)) {
            mStatusChangeListeners.add(listener);
        }
        return this;
    }

    public TyGps removeStatusChangeListener(OnStatusChangeListener listener) {
        if (mStatusChangeListeners != null && mStatusChangeListeners.contains(listener)) {
            mStatusChangeListeners.remove(listener);
        }
        return this;
    }

    public TyGps addNmeaReceivedListener(OnNmeaReceivedListener listener) {
        if (mNmeaReceivedListeners == null) {
            mNmeaReceivedListeners = new ArrayList<>();
        }
        if (!mNmeaReceivedListeners.contains(listener)) {
            mNmeaReceivedListeners.add(listener);
        }
        return this;
    }

    public TyGps removeNmeaReceivedListener(OnNmeaReceivedListener listener) {
        if (mNmeaReceivedListeners != null && mNmeaReceivedListeners.contains(listener)) {
            mNmeaReceivedListeners.remove(listener);
        }
        return this;
    }

    private void removeAllListener() {
        if (mLocationChangeListeners != null) {
            mLocationChangeListeners.clear();
        }
        if (mStatusChangeListeners != null) {
            mStatusChangeListeners.clear();
        }
        if (mNmeaReceivedListeners != null) {
            mNmeaReceivedListeners.clear();
        }
    }

    public TyGps setAddressParse(AddressParse parse){
        this.mAddressParse = parse;
        return this;
    }

    public List<Address> getAddress() {
        return getAddress(getLongitude(), getLatitude());
    }

    public List<Address> getAddress(double longitude, double latitude) {
        if (mAddressParse != null) {
            return mAddressParse.getAddress(longitude, latitude);
        }
        return null;
    }

    public Location getLocation() {
        return mCurLocation;
    }

    public LonLat getLatLon() {
        return getLatLon(CoordType.WGS84);
    }

    public LonLat getLatLon(CoordType type) {
        if (mCurLocation == null) {
            return new LonLat(0, 0);
        }
        LonLat lonLat;
        switch (type) {
            case WGS84:
                lonLat = new LonLat(mCurLocation.getLongitude(), mCurLocation.getLatitude(), CoordType.WGS84);
                break;
            case GCJ02:
                lonLat = CoorsTransform.GPS84ToGCJ02(mCurLocation.getLongitude(), mCurLocation.getLatitude());
                break;
            case BD09:
                lonLat = CoorsTransform.GPS84ToBD09(mCurLocation.getLongitude(), mCurLocation.getLatitude());
                break;
            default:
                lonLat = new LonLat(mCurLocation.getLongitude(), mCurLocation.getLatitude(), CoordType.UNKNOWN);
                break;
        }
        return lonLat;
    }

    public double getLongitude() {
        return getLatLon().getLongitude();
    }

    public double getLongitude(CoordType type) {
        return getLatLon(type).getLongitude();
    }

    public double getLatitude() {
        return getLatLon().getLatitude();
    }

    public double getLatitude(CoordType type) {
        return getLatLon(type).getLatitude();
    }

    public double getBearing() {
        return mCurLocation == null ? 0 : mCurLocation.getBearing();
    }

    public double getSpeed() {
        return mCurLocation == null ? 0 : mCurLocation.getSpeed();
    }

    public double getAltitude() {
        return mCurLocation == null ? 0 : mCurLocation.getAltitude();
    }

    public double getAccuracy() {
        return mCurLocation == null ? 0 : mCurLocation.getAccuracy();
    }

    public long getGpsTime() {
        return mCurLocation == null ? 0 : mCurLocation.getTime();
    }

    public String getProvider() {
        return mCurLocation == null ? "" : mCurLocation.getProvider();
    }

    public boolean isFromMockProvider() {
        return mCurLocation == null ? false : mCurLocation.isFromMockProvider();
    }

    /**
     * 设置定位参数
     *
     * @return {@link Criteria}
     */
    private Criteria getDefaultCriteria() {
        Criteria criteria = new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //设置是否要求速度
        criteria.setSpeedRequired(true);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(true);
        //设置是否需要方位信息
        criteria.setBearingRequired(true);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(true);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    /**
     * 判断Gps是否可用
     *
     * @return true: 是 false: 否
     */
    public boolean isGpsEnabled() {
        LocationManager lm = (LocationManager) TyTool.getInstance().getContext().getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 判断Gps是否可用
     *
     * @return true: 是 false: 否
     */
    public boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 判断定位是否可用
     *
     * @return true: 是 false: 否
     */
    public boolean isLocationEnabled() {
        LocationManager lm = (LocationManager) TyTool.getInstance().getContext().getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 判断定位是否可用
     *
     * @return true: 是 false: 否
     */
    public boolean isLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 打开Gps设置界面
     */
    public void openGpsSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TyTool.getInstance().getContext().startActivity(intent);
    }

    /**
     * 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
     *
     * @param location 坐标
     */
    @Override
    public void onLocationChanged(Location location) {
        if (mIsFilterMock && location.isFromMockProvider()) {
            return;
        }
        if (mLocationChangeListeners != null) {
            for (OnLocationChangeListener listener : mLocationChangeListeners) {
                listener.onLocationChanged(location);
            }
        }
        if (mIsGpsToFile && mGpsToFilePath != null) {
            if (!FileUtils.isFileExist(mGpsToFilePath)) {
                LogSaveUtils.saveLog(mGpsToFilePath, "lon,lat,direction,speed,alt,accuracy,gpstime");
            }
            LogSaveUtils.saveLog(mGpsToFilePath, String.format("%.6f,%.6f,%.2f,%.2f,%.2f,%.2f,%s", location.getLongitude(), location.getLatitude(), location.getBearing(), location.getSpeed(), location.getAltitude(), location.getAccuracy(), DateTimeUtils.getDateTime(location.getTime())));
        }
        mCurLocation = location;
    }

    /**
     * provider的在可用、暂时不可用和无服务三个状态直接切换时触发此函数
     *
     * @param provider 提供者
     * @param status   状态
     * @param extras   provider可选包
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (mStatusChangeListeners != null) {
            for (OnStatusChangeListener listener : mStatusChangeListeners) {
                listener.onStatusChanged(provider, status, extras);
            }
        }
    }

    /**
     * provider被enable时触发此函数，比如GPS被打开
     */
    @Override
    public void onProviderEnabled(String provider) {
        if (mStatusChangeListeners != null) {
            for (OnStatusChangeListener listener : mStatusChangeListeners) {
                listener.onProviderEnabled(provider);
            }
        }
    }

    /**
     * provider被disable时触发此函数，比如GPS被关闭
     */
    @Override
    public void onProviderDisabled(String provider) {
        if (mStatusChangeListeners != null) {
            for (OnStatusChangeListener listener : mStatusChangeListeners) {
                listener.onProviderDisabled(provider);
            }
        }
    }

    @Override
    public void onNmeaReceived(long l, String s) {
        if (mNmeaReceivedListeners != null) {
            for (OnNmeaReceivedListener listener : mNmeaReceivedListeners) {
                listener.onNmeaReceived(l, s);
            }
        }
        if (mIsNmeaToFile && mNmeaToFilePath != null) {
            LogSaveUtils.saveLog(mNmeaToFilePath, s);
        }
    }

    /**
     * 位置变化监听
     */
    public interface OnLocationChangeListener {
        /**
         * 获取最后一次保留的坐标
         *
         * @param location 坐标
         */
        void getLastKnownLocation(Location location);

        /**
         * 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
         *
         * @param location 坐标
         */
        void onLocationChanged(Location location);
    }

    /**
     * GPS状态变化监听
     */
    public interface OnStatusChangeListener {
        /**
         * 位置状态发生改变，provider的在可用、暂时不可用和无服务三个状态直接切换时触发此函数
         *
         * @param provider 提供者
         * @param status   状态
         * @param extras   provider可选包
         */
        void onStatusChanged(String provider, int status, Bundle extras);

        /**
         * provider被enable时触发此函数，比如GPS被打开
         */
        void onProviderEnabled(String provider);

        /**
         * provider被disable时触发此函数，比如GPS被关闭
         */
        void onProviderDisabled(String provider);
    }

    /**
     * NMEA报文接收监听
     */
    public interface OnNmeaReceivedListener {
        /**
         * NMEA报文接收
         * @param l 时间戳
         * @param s 报文
         */
        void onNmeaReceived(long l, String s);
    }

    /**
     * 地址解析
     */
    public interface AddressParse {
        /**
         * 获取地址信息
         * @param longitude 经度
         * @param latitude 纬度
         * @return 地址信息
         */
        List<Address> getAddress(double longitude, double latitude);
    }

    /**
     * 通过Geocoder方式获取地址信息
     */
    private class GeocoderAddressParse implements AddressParse {

        @Override
        public List<Address> getAddress(double longitude, double latitude) {
            Geocoder geocoder = new Geocoder(TyTool.getInstance().getContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    return addresses;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
