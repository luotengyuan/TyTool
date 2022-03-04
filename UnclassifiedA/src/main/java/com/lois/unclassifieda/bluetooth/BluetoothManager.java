package com.lois.unclassifieda.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Description: 蓝牙管理
 * User: Luo.T.Y
 * Date: 2017-09-22
 * Time: 9:36
 */
public class BluetoothManager {
    // Debugging
    private static final String TAG = BluetoothManager.class.getSimpleName();

    // Local Bluetooth adapter
    public static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    // Member object for the chat services
    public static BluetoothService mPrintService = null;


    public BluetoothManager() {
        // TODO Auto-generated constructor stub

    }

    /**
     * 检测蓝牙是否已打开
     *
     * @return
     */
    public static boolean bluetoothIsEnabled() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            return true;
        }
        return false;
    }

    public static void bluetoothEnable(Context context, Handler handler) {
        // If BT is not on, request that it be enabled.
        // setupPrintService() will then be called during onActivityResult
        if (mBluetoothAdapter != null && mPrintService == null) {
            mPrintService = new BluetoothService(context, handler);
        }
    }

    public static void bluetoothDisEnable() {
        // If BT is not on, request that it be enabled.
        // setupPrintService() will then be called during onActivityResult
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

    public static void bluetoothEnable() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() == false) {
            mBluetoothAdapter.enable();
        }
    }

    public static synchronized void bluetoothStart() {
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mPrintService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mPrintService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mPrintService.start();
            }
        }
    }

    public static void bluetoothStop() {
        // Stop the Bluetooth chat services
        if (mPrintService != null) {
            mPrintService.stop();
        }
    }


    /**
     * get buletooth status is connected or not.
     */
    public static boolean isBluetoothConnected() {
        if (mPrintService == null || mPrintService.getState() != BluetoothService.STATE_CONNECTED) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * get buletooth devicename.
     */
    public static String getBluetoothDeviceName() {
        if (mPrintService == null || mPrintService.getState() != BluetoothService.STATE_CONNECTED) {
            return "";
        } else {
            return "";//BluetoothSettingActivity.mConnectedDeviceName;
        }
    }

    /**
     * send data to device for print.
     *
     * @param data A string of text to send.
     */
    public static boolean bluetoothPrintData(Context context, String data) {
        // Check that we're actually connected before trying anything
        if (mPrintService == null || mPrintService.getState() != BluetoothService.STATE_CONNECTED) {
            //new WarningView().toast(context, R.string.not_connected);
            return false;
        }

        // Check that there's actually something to send
        if (data != null && data.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = null;
            try {
                send = data.getBytes("GBK");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mPrintService.write(send);
            return true;
        }
        return false;
    }

    /**
     * send image data to device for print.
     *
     * @param imageByte A imageByte to send.
     */
    public static boolean bluetoothPrintImage(Context context, ArrayList<byte[]> imageByte) {
        // Check that we're actually connected before trying anything
        if (mPrintService == null || mPrintService.getState() != BluetoothService.STATE_CONNECTED) {
            //new WarningView().toast(context, R.string.not_connected);
            return false;
        }

        if (getBluetoothDeviceName().contains("MPT-II") || getBluetoothDeviceName().contains("T10-BT")) {
            BluetoothManager.setBluetoothMPTIIprintLineSpace(context, 0); // 设置行间距为0
            for (int i = 0; i < imageByte.size(); i++) {
                if (imageByte.get(i) != null) {
                    BluetoothManager.bluetoothMPTIIprintImageData(context, imageByte.get(i));
                }
            }
            BluetoothManager.setBluetoothMPTIIprintLineSpace(context, -1); // 设置行间距为0
        } else if (getBluetoothDeviceName().contains("T5 BT Printer")) {
            for (int i = 0; i < imageByte.size(); i++) {
                if (imageByte.get(i) != null) {
                    BluetoothManager.bluetoothSPRTprintImageData(context, imageByte.get(i));
                }
            }
        }
        return true;
    }

    /**
     * set MPT-II bluetooth printer line space.
     *
     * @param space: pixel unit . -1 恢复默认
     */
    public static boolean setBluetoothMPTIIprintLineSpace(Context context, int space) {
        byte lineSpace[] = null;//{0x1B, 0x33, 0x00};   // 设置行间距为,单位像素
        // Check that we're actually connected before trying anything
        if (mPrintService == null || mPrintService.getState() != BluetoothService.STATE_CONNECTED) {
            //new WarningView().toast(context, R.string.not_connected);
            return false;
        }

        // Check that there's actually something to send
        if (space < 0) {
            lineSpace = new byte[2];
            lineSpace[0] = 0x1B;
            lineSpace[1] = 0x32;
        } else {
            lineSpace = new byte[3];
            lineSpace[0] = 0x1B;
            lineSpace[1] = 0x33;
            lineSpace[2] = (byte) space;
        }
        mPrintService.write(lineSpace);
        return true;
    }

    /**
     * send bitmap image data to MPT-II device for print.
     *
     * @param data A string of text to send.
     */
    public static boolean bluetoothMPTIIprintImageData(Context context, byte[] data) {
        // Check that we're actually connected before trying anything
        if (mPrintService == null || mPrintService.getState() != BluetoothService.STATE_CONNECTED) {
            //new WarningView().toast(context, R.string.not_connected);
            return false;
        }

        // Check that there's actually something to send
        if (data != null && data.length > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = new byte[data.length + 6];
            try {
                send[0] = 0x1B;   // MPT-II打印黑白点阵位图指令
                send[1] = 0x2A;   // MPT-II打印黑白点阵位图指令
                //send[2] = 0x20;   // m = 32, 高度24点, 两倍宽
                send[2] = 0x00;   // m = 0, 高度8点, 两倍宽
                //int mWidth = data.length/3;
                send[3] = (byte) (data.length % 255); // 打印宽度的低字节;
                send[4] = (byte) (data.length / 255); // 打印宽度的高字节;
                System.arraycopy(data, 0, send, 5, data.length);
                send[data.length + 6 - 1] = 0x0a;    // 走纸
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mPrintService.write(send);
            return true;
        }
        return false;
    }

    /**
     * send bitmap image data to SPRT device for print.
     *
     * @param data A string of text to send.
     */
    public static boolean bluetoothSPRTprintImageData(Context context, byte[] data) {
        // Check that we're actually connected before trying anything
        if (mPrintService == null || mPrintService.getState() != BluetoothService.STATE_CONNECTED) {
            //new WarningView().toast(context, R.string.not_connected);
            return false;
        }

        // Check that there's actually something to send
        if (data.length > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            int i;
            for (i = 0; i < data.length; i++) {// 打印机BUG,当数据全零时不会换行
                if (data[i] != 0) {
                    break;
                }
            }
            if (i == data.length) {
                data[data.length - 1] = 1;    // 整行全为0时,最后一位置为1,否则不会换行
            }
            byte[] send = new byte[data.length + 6];
            try {
                send[0] = 0x1B;   // SPRT打印黑白点阵位图指令
                send[1] = 0x4B;   // SPRT打印黑白点阵位图指令
                send[2] = (byte) (data.length % 256); // 打印宽度的低字节;
                send[3] = (byte) (data.length / 256);
                ; // 打印宽度的高字节;
                System.arraycopy(data, 0, send, 4, data.length);
                send[data.length + 6 - 2] = 0x0a;    // 走纸
                send[data.length + 6 - 1] = 0x0d;    // 走纸
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mPrintService.write(send);
            return true;
        }
        return false;
    }
}
