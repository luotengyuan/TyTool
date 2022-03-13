package com.lois.tytool.tts;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * @Description TTS音频数据回调和播报类
 * @Author Luo.T.Y
 * @Date 2017-09-22
 * @Time 11:32
 */
public class AudioData {
    private static final String TAG = AudioData.class.getSimpleName();

    private static AudioTrack mAudio = null;
    private static int mStreamType = AudioManager.STREAM_MUSIC;
    private static int mSampleRate = 16000;
    private static int mBuffSize = 8000;

    static {
        mAudio = new AudioTrack(mStreamType
                , mSampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO
                , AudioFormat.ENCODING_PCM_16BIT
                , mBuffSize, AudioTrack.MODE_STREAM);
        Log.d(TAG, " AudioTrack create ok");
    }

    /**
     * For C call
     * 底层JNI通过调用该方法将合成好的音频数据传递出来
     * @param len 音频数据长度
     * @param data 音频数据
     */
    public static void onJniOutData(int len, byte[] data) {
        if (null == mAudio) {
            Log.e(TAG, " mAudio null");
            return;
        }
        if (mAudio.getState() != AudioTrack.STATE_INITIALIZED) {
            Log.e(TAG, " mAudio STATE_INITIALIZED");
            return;
        }
        try {
            mAudio.write(data, 0, len);
            mAudio.play();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * For C Watch Call back
     *
     * @param nProcBegin
     */
    public static void onJniWatchCB(int nProcBegin) {
        Log.d(TAG, "onJniWatchCB  process begin = " + nProcBegin);
    }

    public static void close() {
        if (mAudio == null) {
            return;
        }
        mAudio.stop();
    }

}


