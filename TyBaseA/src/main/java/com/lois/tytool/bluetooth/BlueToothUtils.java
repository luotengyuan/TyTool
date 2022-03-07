package com.lois.tytool.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;

import com.lois.tytool.TyTool;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @Description 蓝牙工具类
 * @Author Luo.T.Y
 * @Date 2017-09-22
 * @Time 11:32
 */
public class BlueToothUtils {
    private static final String TAG = BlueToothUtils.class.getSimpleName();
    /**
     * 蓝牙适配器
     */
    private BluetoothAdapter mBluetoothAdapter;
    /**
     * 蓝牙是否可用
     */
    private boolean mBleEnable;

    private BluetoothManager mBluetoothManager;

    public BlueToothUtils() {
        mBluetoothManager = (BluetoothManager) TyTool.getInstance().getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBleEnable = checkBlueToothEnable();
    }

    private static class SingletonInstance {
        private static final BlueToothUtils INSTANCE = new BlueToothUtils();
    }

    public static BlueToothUtils getInstance() {
        return SingletonInstance.INSTANCE;
    }

    //<uses-permission android:name="android.permission.BLUETOOTH"/>
    @SuppressWarnings("MissingPermission")
    public static String getBluetoothMAC(Context context) {
        String result = null;
        try {
            if (context.checkCallingOrSelfPermission(Manifest.permission.BLUETOOTH)
                    == PackageManager.PERMISSION_GRANTED) {
                BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
                result = bta.getAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 检测设备是否支持蓝牙
     */
    public boolean checkBlueToothEnable() {
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "该设备不支持蓝牙");
            return false;
        } else {
            return true;
        }
    }

    /**
     * 让用户去设置蓝牙
     */
    public void setBlueTooth(Activity activity) {
        if (mBleEnable) {
            Intent blueTooth = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            activity.startActivity(blueTooth);
        }
    }

    /**
     * 打开蓝牙
     */
    public void onBlueTooth() {
        if (mBleEnable) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
        }
    }

    /**
     * 关闭蓝牙
     */
    public void offBlueTooth() {
        if (mBleEnable) {
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
            }
        }
    }

    /**
     * 获取已经配对的设备
     */
    public Set<BluetoothDevice> getConnectedDevices() {
        if (mBleEnable) {
            if (mBluetoothAdapter.isEnabled()) {
                return mBluetoothAdapter.getBondedDevices();
            }
        }
        return null;
    }

    /**
     * 设置可发现模式时长
     * 默认情况下，设备的可发现模式会持续120秒。
     * 通过给Intent对象添加EXTRA_DISCOVERABLE_DURATION附加字段，可以定义不同持续时间。
     * 应用程序能够设置的最大持续时间是3600秒
     */
    public void setDiscoverableDuration(Activity activity, int duration) {
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //定义持续时间
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
        activity.startActivity(discoverableIntent);
    }

    /**
     * 扫描蓝牙，会走广播
     */
    public void startDiscovery() {
        if (mBleEnable) {
            if (!mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.startDiscovery();
                Log.v(TAG, "开始发现");
            }
        }
    }

    /**
     * 停止扫描
     */
    public void stopDiscovery() {
        if (mBleEnable) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }
    }

    /**
     * 扫描蓝牙
     */
    public void startScan() {
        if (mBleEnable && mBluetoothAdapter.getBluetoothLeScanner() != null) {
            mBluetoothAdapter.getBluetoothLeScanner().startScan(new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    Log.v(TAG, "onScanResult");
                    super.onScanResult(callbackType, result);
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                    Log.v(TAG, "onBatchScanResults");
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.v(TAG, "onScanFailed");
                }
            });
        }
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        if (mBleEnable && mBluetoothAdapter.getBluetoothLeScanner() != null) {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    Log.v(TAG, "onScanResult");
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                    Log.v(TAG, "onBatchScanResults");
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.v(TAG, "onScanFailed");
                }
            });
        }
    }

    /**
     * 连接设备
     */
    public void connectGatt(Activity activity, final BluetoothDevice device) {
        stopDiscovery();
        if (mBleEnable) {
            device.connectGatt(activity, true, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    switch (status) {
                        case BluetoothGatt.GATT_SUCCESS:
                            //连接成功
                            break;
                        case BluetoothProfile.STATE_CONNECTED:
                            //发现蓝牙服务
                            break;
                        default:
                            break;
                    }
                    super.onConnectionStateChange(gatt, status, newState);
                }
            });
        }
    }

    /**
     * 判断蓝牙耳机是否连接
     *
     * @return
     */
    private boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (BluetoothProfile.STATE_CONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
            return true;
        }
        return false;
    }

    /**
     * 距离计算公式：以目前最流行的蓝牙信标标准（苹果主推的iBeacon）官方公式为例
     * d = 10^((abs(RSSI) - A) / (10 * n))
     * 其中
     * d - 计算所得距离；RSSI - 接收信号强度（负值）
     * A - 发射端和接收端相隔1米时的信号强度
     * n - 环境衰减因子
     * 传入RSSI值，返回距离（单位：米）其中，A参数赋了59，n赋了2.0。
     * 当不知道周围蓝牙设备准确位置时，只能给A和n赋经验值.
     *
     * @param rssi
     * @return
     */
    public double getDist(int rssi) {
        int iRssi = Math.abs(rssi);
        double power = (iRssi - 59) / (10 * 2.0);
        return Math.pow(10, power);
    }
}

