package com.lois.tytool.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * @Description 短信接收广播
 * @Author Luo.T.Y
 * @Date 2017-09-23
 * @Time 9:32
 */
public class SmsReceive extends BroadcastReceiver {
    private static final String TAG = SmsReceive.class.getSimpleName();
    private static SmsReceive mSmsRece;

    /**
     * 获取实例
     *
     * @return
     */
    public static SmsReceive getInstance() {
        if (mSmsRece == null) {
            mSmsRece = new SmsReceive();
        }
        return mSmsRece;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Object messages[] = (Object[]) bundle.get("pdus");
            SmsMessage smsMessage[] = new SmsMessage[messages.length];
            for (int n = 0; n < messages.length; n++) {
                //还原短信内容
                smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
                //取得短信内容
                String msgBody = smsMessage[n].getMessageBody();
                Log.e(TAG, "msgBody=" + msgBody);
                this.abortBroadcast();
            }
        } catch (Exception e) {
            // SMSTest.setRMsg(e.toString());
        }
    }


    /**
     * 界面关闭时取消注册监听
     *
     * @param context
     */
    public void unRegister(Context context) {
        context.unregisterReceiver(mSmsRece);
    }
}
