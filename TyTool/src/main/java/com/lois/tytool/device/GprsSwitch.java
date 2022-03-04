package com.lois.tytool.device;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Description: 数据流量开关
 * User: Luo.T.Y
 * Date: 2017-10-31
 * Time: 16:16
 */
public class GprsSwitch {
    private static final String TAG = GprsSwitch.class.getSimpleName();

    /**
     * 返回手机移动数据的状态(5.0之前之后都可以使用此方法判断数据流量是否打开)
     *
     * @param context
     * @param arg     默认填null
     * @return true 连接 false 未连接
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean getGprsState(Context context, Object[] arg) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();
            Class[] argsClass = null;
            if (arg != null) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }
            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);
            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);
            return isOpen;
        } catch (Exception e) {
            return false;
        }
    }

    // 5.0之前可以使用以下方法开启/关闭数据流量
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static boolean setGprsEnabled(Context context, boolean enabled) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();
            Class[] argsClass = new Class[1];
            argsClass[0] = boolean.class;
            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);
            method.invoke(mConnectivityManager, enabled);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            return false;
        }
    }

    /**
     * 针对Android5.1设置数据流量开关
     * https://zhidao.baidu.com/question/1885040895227322828.html
     *
     * @param context
     * @param enabled
     */
    public static boolean setMobileDataState(Context context, boolean enabled) {
        TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method setDataEnabled = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            if (null != setDataEnabled) {
                setDataEnabled.invoke(telephonyService, enabled);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            return false;
        }
    }

    /**
     * 针对Android5.1获取数据流量开关
     * https://zhidao.baidu.com/question/1885040895227322828.html
     *
     * @param context
     */
    public static boolean getMobileDataState(Context context) {
        TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method getDataEnabled = telephonyService.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getDataEnabled) {
                return (Boolean) getDataEnabled.invoke(telephonyService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置数据流量开关
     *
     * @param context
     * @param enabled
     * @return
     */
    public static boolean setGprsSwitch(Context context, boolean enabled) {
        if (Build.VERSION.SDK_INT >= 21) {
            return setMobileDataState(context, enabled);
        } else {
            return setGprsEnabled(context, enabled);
        }
    }

}
