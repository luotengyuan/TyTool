package com.lois.tytool.sms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * @Description 发送短信
 * @Author Luo.T.Y
 * @Date 2017-09-23
 * @Time 9:35
 */
public class SmsSend {
    private static final String TAG = SmsSend.class.getSimpleName();
    private static SmsSend mSmsSend = null;
    private static BroadcastReceiver mBroadcastReceiver;
    private static BroadcastReceiver mBroadcastDeliverReceiver;

    /**
     * 获取实例
     *
     * @return
     */
    public static SmsSend getInstance() {
        if (mSmsSend == null) {
            mSmsSend = new SmsSend();
        }
        return mSmsSend;
    }

    /**
     * 发送短信
     *
     * @param phoneNumber
     * @param message
     */
    public void sendSMS(final Context context, String phoneNumber, String message) {

        SmsManager sms = SmsManager.getDefault();

        Intent sentIntent = new Intent("SMS_SEND_ACTION");
        Intent deliverIntent = new Intent("SMS_DELIVERED_ACTION");

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, sentIntent, 0);
        PendingIntent deliverPI = PendingIntent.getBroadcast(context, 0, deliverIntent, 0);

        // 注册广播
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context _context, Intent _intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Log.e(TAG, "SMS sent success actions");
                            //Toast.makeText(context, "SMS sent success actions", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Log.e(TAG, "SMS generic failure actions");
                            //Toast.makeText(context, "SMS generic failure actions", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                }
            };
        }
        if (mBroadcastDeliverReceiver == null) {
            mBroadcastDeliverReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context _context, Intent _intent) {
                    Toast.makeText(context, "SMS delivered actions", Toast.LENGTH_SHORT).show();
                }
            };
        }
        context.registerReceiver(mBroadcastReceiver, new IntentFilter("SMS_SEND_ACTION"));
        context.registerReceiver(mBroadcastDeliverReceiver, new IntentFilter("SMS_DELIVERED_ACTION"));

        if (message.length() > 70) {
            // 短信度大于70字  拆分信息
            ArrayList<String> msgs = sms.divideMessage(message);
            for (String msg : msgs) {
                sms.sendTextMessage(phoneNumber, null, msg, sentPI, deliverPI);
            }
        } else {
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliverPI);
        }
    }

    /**
     * 界面关闭时取消注册监听
     *
     * @param context
     */
    public void unRegister(Context context) {
        context.unregisterReceiver(mBroadcastDeliverReceiver);
        context.unregisterReceiver(mBroadcastReceiver);
    }

    public static void sendMessage(Handler handler, int i) {
        if (handler == null) {
            return;
        }
        Message.obtain(handler, i).sendToTarget();
    }

    public static void sendMessage(Handler handler, int i, int j, int k) {
        if (handler == null) {
            return;
        }
        Message.obtain(handler, i, j, k).sendToTarget();
    }

    public static void sendMessage(Handler handler, int i, int j, int k,
                                   Object obj) {
        if (handler == null) {
            return;
        }
        Message.obtain(handler, i, j, k, obj).sendToTarget();
    }

    public static void sendMessage(Handler handler, int i, Object obj) {
        if (handler == null) {
            return;
        }
        Message.obtain(handler, i, obj).sendToTarget();
    }

    public static void sendMessageDelayed(Handler handler, int i, int j) {
        if (handler == null) {
            return;
        }
        handler.sendMessageDelayed(Message.obtain(handler, i), j);
    }
}
