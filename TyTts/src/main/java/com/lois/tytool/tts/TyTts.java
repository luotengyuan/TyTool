package com.lois.tytool.tts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lois.tytool.base.secert.Md5Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @Description TyTts工具类
 * 初始化
 * 参数设置
 * 播放设置：是否需要打断、是否可用等待、最长等待时长
 * @Author Luo.T.Y
 * @Date 2022/3/3
 * @Time 14:56
 */
public class TyTts {
    private static final String TAG = TyTts.class.getSimpleName();

    /**
     * 语音包文件MD5校验值，如果语音包有替换，此值也需要跟着换
     */
    private static final String RESOURCE_MD5 = "c69752174ca4c5a9131f1e8fb9a6d97a";

    /**
     * 默认的最大延时时间（毫秒）
     */
    private static final int DEFAULT_MAX_DELAY_TIME = 30000;

    /**
     * TTS引擎是否初始化成功
     */
    private boolean isInit = false;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 语音队列
     */
    private PriorityBlockingQueue<AudioElement> audioQueue;
    /**
     * 播放语音线程
     */
    private PlayThread playThread;
    /**
     * 是否引导音正在播放
     */
    private boolean isTonePlaying = false;
    /**
     * 是否tts语音正在播放
     */
    private boolean isTtsPlaying = false;
    /**
     * 引导音播放实例
     */
    private MediaPlayer mediaPlayer;
    /**
     * 回调接口
     */
    private TtsCallback mTtsCallback;
    /**
     * 当前正在播放的内容
     */
    private AudioElement curAudio;

    /**
     * 获取单例
     *
     * @return 单例对象
     */
    public static TyTts getInstance() {
        return TyTtsHolder.INSTANCE;
    }

    /**
     * 单例持有者
     */
    private static class TyTtsHolder {
        @SuppressLint("StaticFieldLeak")
        private static final TyTts INSTANCE = new TyTts();
    }

    /**
     * 初始化对象
     *
     * @param context 上下文
     * @return 是否初始化成功
     */
    public boolean init(Context context) {
        if (isInit) {
            return true;
        }
        if (context == null) {
            Log.w(TAG, "Context不能为空");
            return false;
        }
        mContext = context;
        String fp = getFilesPath(context);
        if (fp == null) {
            Log.w(TAG, "TTS文件夹获取失败");
            return false;
        }
        // 初始化资源文件
        boolean resInitOk = true;
        // 离线资源文件路径
        String mResourceFilePath = getFilesPath(context) + "/TTS/resource.tts";
        File file = new File(mResourceFilePath);
        String localMd5 = null;
        try {
            localMd5 = Md5Utils.getMd5ByFile(file);
            Log.i(TAG, "localMd5 = " + localMd5);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (!RESOURCE_MD5.equals(localMd5)) {
            Log.w(TAG, "语音包资源文件MD5校验失败  localMd5 = " + localMd5 + "  RESOURCE_MD5 = " + RESOURCE_MD5);
            if (file.exists()) {
                file.delete();
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            // 复制license到指定路径
            InputStream resourceInputStream = context.getResources().openRawResource(R.raw.resource);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = resourceInputStream.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (Exception e) {
                e.printStackTrace();
                resInitOk = false;
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    resourceInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!resInitOk) {
            Log.w(TAG, "TTS资源文件初始化失败");
            return false;
        }

        int ret = TtsJni.JniCreate(mResourceFilePath);
        if (ret == 0) {
            Log.i(TAG, "TTS引擎初始化成功");
            // 默认语音设置
            TtsJni.JniSetParam(0x100, 1);
            /* 角色参数。晓燕（女声） */
            TtsJni.JniSetParam(0x500, 3);
            TtsJni.JniSetParam(0x102, 1);
            audioQueue = new PriorityBlockingQueue<AudioElement>(10, comparator);
            if (playThread == null) {
                playThread = new PlayThread();
                playThread.start();
            }
            isInit = true;
            return true;
        } else {
            Log.w(TAG, "TTS引擎初始化失败");
            isInit = false;
            return false;
        }
    }

    /**
     * 销毁TTS服务
     */
    public void destory() {
        if (isInit) {
            TtsJni.JniDestory();
            if (audioQueue != null) {
                audioQueue.clear();
            }
            audioQueue = null;
            if (playThread != null && !playThread.isInterrupted()) {
                playThread.interrupt();
            }
            playThread = null;
            isInit = false;
        }
    }

    /**
     * 语音队列的对比器，让等级数字小的排在队列前面
     */
    private final Comparator<AudioElement> comparator = new Comparator() {
        @Override
        public int compare(Object lhs, Object rhs) {
            AudioLevel lhsPri = ((AudioElement) lhs).getLevel();
            AudioLevel rhsPri = ((AudioElement) rhs).getLevel();
            if (lhsPri.ordinal() < rhsPri.ordinal()) { // 数值小优先级高
                return 1;
            } else if (lhsPri.ordinal() > rhsPri.ordinal()) {
                return -1;
            }
            return 0;
        }
    };

    /**
     * 获取程序文件存储目录，优先使用外部存储，再使用内部存储
     *
     * @param context 上下文
     * @return 目录路径
     */
    private String getFilesPath(Context context) {
        String filePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            filePath = context.getExternalFilesDir(null).getPath();
        } else {
            //外部存储不可用
            filePath = context.getFilesDir().getPath();
        }
        return filePath;
    }

    /**
     * 设置回调接口
     *
     * @param callback 接口
     * @return 实例本身
     */
    public TyTts setCallback(TtsCallback callback) {
        this.mTtsCallback = callback;
        return this;
    }

    /**
     * 清除回调接口
     *
     * @return 实例本身
     */
    public TyTts removeCallback() {
        this.mTtsCallback = null;
        return this;
    }

    /**
     * 语速参数
     * constants for values of parameter ivTTS_PARAM_VOICE_SPEED
     * the range of voice speed value is from -32768 to +32767
     * -32768		slowest voice speed
     * 0			normal voice speed (default)
     * +32767		fastest voice speed
     *
     * @param value 语速
     * @return 返回对象本身
     */
    public TyTts setVoiceSpeed(int value) {
        if (isInit) {
            TtsJni.JniSetParam(0x502, value);
        }
        return this;
    }

    /**
     * 语调参数
     * constants for values of parameter ivTTS_PARAM_VOICE_PITCH
     * the range of voice tone value is from -32768 to +32767
     * -32768		lowest voice tone
     * 0			normal voice tone (default)
     * +32767		highest voice tone
     *
     * @param value 语调
     * @return 返回对象本身
     */
    public TyTts setVoicePitch(int value) {
        if (isInit) {
            TtsJni.JniSetParam(0x503, value);
        }
        return this;
    }

    /**
     * 音量参数
     * constants for values of parameter ivTTS_PARAM_VOLUME
     * the range of volume value is from -32768 to +32767
     * -32768		minimized volume
     * 0			normal volume
     * +32767		maximized volume (default)
     *
     * @param value 音量
     * @return 返回对象本身
     */
    public TyTts setVolume(int value) {
        if (isInit) {
            TtsJni.JniSetParam(0x504, value);
        }
        return this;
    }

    /**
     * 声音特效预置模式
     * constants for values of parameter ivTTS_PARAM_VEMODE
     * 0			关闭声音特效（默认值）。
     * 1			忽远忽近。
     * 2			回声。
     * 3			机器人。
     * 4			合唱。
     * 5			水下。
     * 6			混响。
     * 7			阴阳怪气。
     *
     * @param value 特效类型
     * @return 对象本身
     */
    public TyTts setVeMode(int value) {
        if (isInit && value >= 0 && value <= 7) {
            TtsJni.JniSetParam(0x600, value);
        }
        return this;
    }

    /**
     * 应用场景
     * constants for values of parameter ivTTS_PARAM_USERMODE(ivTTS_PARAM_NAVIGATION_MODE)
     * 0			使用普通模式合成。
     * 1			使用导航模式合成。
     * 2			使用手机模式合成。
     * 3			使用教育模式合成。
     *
     * @param value 应用场景
     * @return 对象本身
     */
    public TyTts setUserMode(int value) {
        if (isInit && value >= 0 && value <= 3) {
            TtsJni.JniSetParam(0x701, value);
        }
        return this;
    }

    /**
     * 设置参数
     * @param paramId 参数ID
     * @param value 参数值
     * @return 对象本身
     */
    public TyTts setParam(int paramId, int value) {
        if (isInit) {
            TtsJni.JniSetParam(paramId, value);
        }
        return this;
    }

    /**
     * 播放线程
     */
    class PlayThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted() && isInit) {
                try {
                    if (isTonePlaying || isTtsPlaying) {
                        sleep(100);
                        continue;
                    }
                    int cacheSize = audioQueue.size();
                    Log.v(TAG, "audio play cache size = " + cacheSize);
                    if (cacheSize > 10) {
                        Log.w(TAG, "Too much audio play cache data!  cacheSize = " + cacheSize);
                    }
                    curAudio = audioQueue.take();
                    Log.i(TAG, "take: " + curAudio.toString());
                    long difTime = System.nanoTime() - curAudio.getTimestamp();
                    Log.i(TAG, "difTime: " + difTime);
                    if (curAudio.isCanDelay()) {
                        if (curAudio.getMaxDelayTime() > 0 && difTime >= curAudio.getMaxDelayTime()) {
                            Log.i(TAG, "can delay but timeout, difTime: " + difTime);
                            sleep(100);
                            continue;
                        }
                    } else {
                        if (difTime > 5000000000L) {
                            Log.i(TAG, "can not delay but timeout( >5s ), difTime: " + difTime);
                            sleep(100);
                            continue;
                        }
                    }
                    if (mTtsCallback != null) {
                        mTtsCallback.onTtsStatusChanged(TtsStatus.START, curAudio);
                    }
                    if (curAudio.isPlayTone()) {
                        Log.i(TAG, "play tone");
                        playPreTone();
                    } else {
                        Log.i(TAG, "not play tone");
                        playTts(curAudio);
                    }
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
            }
        }
    }

    /**
     * 播放引导提示音
     */
    private void playPreTone() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(mContext, R.raw.didi);
        }
        if (mediaPlayer == null) {
            Log.w(TAG, "MediaPlayer create fail!");
            return;
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "preTone onCompletion:" + System.nanoTime());
                if (mediaPlayer != null) {
                    mp.reset();
                    mp.release();//释放音频资源
                    mediaPlayer = null;
                    // 播放完引导音后开始播放tts
                    playTts(curAudio);
                    isTonePlaying = false;
                }
            }
        });
        mediaPlayer.stop();
        try {
            mediaPlayer.prepare();
            Log.i(TAG, "preTone start:" + System.nanoTime());
            mediaPlayer.start();
            isTonePlaying = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    /**
     * 停止引导提示音
     */
    private void stopPreTone() {
        Log.i(TAG, "stopPreTone");
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
            mediaPlayer.release();//释放音频资源
            mediaPlayer = null;
        }
        isTonePlaying = false;
    }

    /**
     * 播放TTS语音
     *
     * @param audioElement 播报内容
     */
    private void playTts(AudioElement audioElement) {
        startReadThread(audioElement.getText());
        Log.i(TAG, "playTts: " + audioElement.getText());
        isTtsPlaying = true;
    }

    /**
     * 停止TTS语音
     */
    private void stopTts() {
        Log.i(TAG, "stopTts");
        TtsJni.JniStop();
        isTtsPlaying = false;
    }

    /**
     * 往队列里面添加播放语音信息
     *
     * @param message 播报内容
     * @return 是否添加到语音播报队列成功
     */
    public boolean play(String message) {
        return play(message, AudioLevel.AUDIO_LEVEL_3_GENERAL, false, DEFAULT_MAX_DELAY_TIME);
    }

    /**
     * 往队列里面添加播放语音信息
     *
     * @param message 播报内容
     * @param level   等级
     * @return 是否添加到语音播报队列成功
     */
    public boolean play(String message, AudioLevel level) {
        return play(message, level, false, DEFAULT_MAX_DELAY_TIME);
    }

    /**
     * 往队列里面添加播放语音信息
     *
     * @param message 播报内容
     * @param level   等级
     * @param isTone  是否播报前置提醒
     * @return 是否添加到语音播报队列成功
     */
    public boolean play(String message, AudioLevel level, boolean isTone) {
        return play(message, level, isTone, DEFAULT_MAX_DELAY_TIME);
    }

    /**
     * 往队列里面添加播放语音信息
     *
     * @param message      播报内容
     * @param level        等级
     * @param isTone       是否播报前置提醒
     * @param maxDelayTime 最大延迟时间（单位：毫秒）
     * @return 是否添加到语音播报队列成功
     */
    public synchronized boolean play(String message, AudioLevel level, boolean isTone, int maxDelayTime) {
        if (!isInit || message == null) {
            Log.w(TAG, "not started or message is null");
            return false;
        }
        if (isTonePlaying || isTtsPlaying) {
            switch (level) {
                case AUDIO_LEVEL_1_RADICAL:
                    if (curAudio != null && curAudio.getLevel() != AudioLevel.AUDIO_LEVEL_1_RADICAL) {
                        Log.w(TAG, "stop playing and play");
                        if (mTtsCallback != null) {
                            mTtsCallback.onTtsStatusChanged(TtsStatus.BREAK, curAudio);
                        }
                        stopPreTone();
                        stopTts();
                        addElement(message, level, isTone, maxDelayTime);
                        return true;
                    } else {
                        Log.w(TAG, "can not stop playing, LEVEL_1 skip");
                        return false;
                    }
                case AUDIO_LEVEL_2_REALTIME:
                    Log.w(TAG, "can not stop playing, LEVEL_2 skip");
                    return false;
                case AUDIO_LEVEL_3_GENERAL:
                    Log.i(TAG, "LEVEL_3 add element");
                    addElement(message, level, isTone, maxDelayTime);
                    return true;
                default:
                    Log.w(TAG, "error LEVEL skip");
                    return false;
            }
        } else {
            Log.i(TAG, "not playing, add element");
            addElement(message, level, isTone, maxDelayTime);
            return true;
        }
    }

    /**
     * 往播放队列里面添加元素
     */
    private void addElement(String message, AudioLevel level, boolean isTone, int maxDelayTime) {
        if (audioQueue == null || message == null || !isInit) {
            return;
        }
        boolean canDelay;
        if (level == AudioLevel.AUDIO_LEVEL_1_RADICAL || level == AudioLevel.AUDIO_LEVEL_2_REALTIME) {
            canDelay = false;
            maxDelayTime = 0;
        } else {
            canDelay = true;
        }
        AudioElement element = new AudioElement(message, level, isTone, canDelay, maxDelayTime * 1000000L, System.nanoTime());
        audioQueue.put(element);
        Log.i(TAG, "put element to queue!");
    }

    /**
     * 是否正在播报TTS语音
     * @return 是否正在播放
     */
    public boolean isPlaying() {
        return isInit && (isTonePlaying || isTtsPlaying);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    break;
                case 2://播放完成
                    if (mTtsCallback != null) {
                        mTtsCallback.onTtsStatusChanged(TtsStatus.FINISH, curAudio);
                    }
                    isTtsPlaying = false;
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 开启播报线程
     *
     * @param text 播报内容
     */
    private synchronized void startReadThread(final String text) {
        class TtsRunThread implements Runnable {
            @Override
            public void run() {
                TtsJni.JniSpeak(text);
                int msgType = TtsJni.JniIsPlaying();
                Message sMsg = mMsgHandler.obtainMessage(msgType);
                mMsgHandler.sendMessageDelayed(sMsg, 0);
            }
        }
        Thread ttsRun = (new Thread(new TtsRunThread()));
        ttsRun.setPriority(Thread.MAX_PRIORITY);
        ttsRun.start();
    }
}
