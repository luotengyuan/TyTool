package com.lois.tytool.network;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Description wifi热点
 * @Author Luo.T.Y
 * @Date 2017-09-22
 * @Time 20:12
 */
public class WifiHotsSpot {
    private static final String TAG = WifiHotsSpot.class.getSimpleName();
    public static final int WIFI_AP_STATE_DISABLING = 0;
    public static final int WIFI_AP_STATE_DISABLED = 1;
    public static final int WIFI_AP_STATE_ENABLING = 2;
    public static final int WIFI_AP_STATE_ENABLED = 3;
    public static final int WIFI_AP_STATE_FAILED = 4;
    public WifiManager wifiManager;
    private Context mContext;

    //网络连接列表
    private List<WifiConfiguration> wifiConfiguration;

    /**
     * 构造函数初始化
     */
    public WifiHotsSpot(Context context) {
        this.mContext = context;
    }

    /**
     * 初始化
     */
    public void init() {
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 打开热点
     */
    public void oPenWifiHotsSpot() {
        if (!isApEnabled()) {
            setWifiApEnabled(true);
        }
    }

    /**
     * 关闭热点
     */
    public void closeWifiHotsSpot() {
        if (isApEnabled()) {
            setWifiApEnabled(false);
        }
    }

    /**
     * 设置热点
     *
     * @param enabled
     * @return
     */
    public boolean setWifiApEnabled(boolean enabled) {
        if (enabled) { // disable WiFi in any case
            wifiManager.setWifiEnabled(false);
        }

        try {
            WifiConfiguration apConfig = new WifiConfiguration();
            apConfig.SSID = "YXTbox";
            apConfig.preSharedKey = "yxtbox11";
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            return (Boolean) method.invoke(wifiManager, apConfig, enabled);
        } catch (Exception e) {
            Log.e("HttpRequest", "Cannot set WiFi AP state", e);
            return false;
        }
    }

    /**
     * 获取热点使能
     *
     * @return
     */
    public boolean isApEnabled() {
        int state = getWifiApState();
        return WIFI_AP_STATE_ENABLING == state || WIFI_AP_STATE_ENABLED == state;
    }

    /**
     * 获取WiFi热点状态
     *
     * @return
     */
    public int getWifiApState() {
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            return (Integer) method.invoke(wifiManager);
        } catch (Exception e) {
            Log.e(TAG, "Cannot get WiFi AP state", e);
            return WIFI_AP_STATE_FAILED;
        }
    }

    //连接GossipDog
    public void connectAP() {
        WifiConfiguration gossipDog = new WifiConfiguration();
        for (WifiConfiguration ap : wifiConfiguration) {
            if (ap.SSID == "YXTbox") {
                gossipDog = ap;
            }
        }

        if (gossipDog != null) {
            gossipDog.preSharedKey = "yxtbox11";
            gossipDog.networkId = wifiManager.addNetwork(gossipDog);
            wifiManager.enableNetwork(gossipDog.networkId, true);
            //result.setText("连接AP成功");
        }
    }
}
