package com.lois.tytool.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * @Description 扬声器
 * @Author Luo.T.Y
 * @Date 2017-09-23
 * @Time 9:59
 */
public class SpeakerUtils {
    private static final String TAG = SpeakerUtils.class.getSimpleName();
    private static AudioManager audioManager;

    private static int currVolume = 0;

    // 打开扬声器
    @SuppressLint("WrongConstant")
    public static void OpenSpeaker(Context ctx) {
        try {
            // 判断扬声器是否在打开
            audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            // 获取当前通话音量
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                Log.e(TAG, "打开扬声器");
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭扬声器
    public static void CloseSpeaker(Context ctx) {
        try {
            audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
