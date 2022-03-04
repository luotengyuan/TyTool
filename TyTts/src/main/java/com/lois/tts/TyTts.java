package com.lois.tts;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lois.tytool.basej.secert.Md5Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
    public static String RESOURCE_MD5 = "c69752174ca4c5a9131f1e8fb9a6d97a";

    /**
     * 离线资源文件路径
     */
    private String mResourceFilePath;
    /**
     * TTS引擎是否初始化成功
     */
    private boolean isInit = false;

    public static TyTts getInstance() {
        return TyTtsHolder.sInstance;
    }

    private static class TyTtsHolder {
        private static final TyTts sInstance = new TyTts();
    }

    public boolean init(Context context) {
        if (isInit) {
            return true;
        }
        if (context == null) {
            Log.w(TAG, "Context不能为空");
            return false;
        }
        String fp = getFilesPath(context);
        if (fp == null) {
            Log.w(TAG, "TTS文件夹获取失败");
            return false;
        }
        // 初始化资源文件
        boolean res_init_ok = true;
        mResourceFilePath = getFilesPath(context) + "/TTS/resource.tts";
        File file = new File(mResourceFilePath);
        String local_md5 = null;
        try {
            local_md5 = Md5Utils.getMd5ByFile(file);
            Log.i(TAG, "local_md5 = " + local_md5);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (!RESOURCE_MD5.equals(local_md5)){
            Log.w(TAG, "语音包资源文件MD5校验失败  local_md5 = " + local_md5 + "  RESOURCE_MD5 = " + RESOURCE_MD5);
            if (file.exists()) {
                file.delete();
            }
            if (!file.getParentFile().exists()){
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
                res_init_ok = false;
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
        if (!res_init_ok) {
            Log.w(TAG, "TTS资源文件初始化失败");
            return false;
        }

        int ret = TtsJni.JniCreate(mResourceFilePath);
        if (ret == 0) {
            Log.i(TAG, "TTS引擎初始化成功");
            // 默认语音设置
            TtsJni.JniSetParam(256, 1);
            TtsJni.JniSetParam(1280, 3);
            TtsJni.JniSetParam(0x102, 1);
            isInit = true;
            return true;
        } else {
            Log.w(TAG, "TTS引擎初始化失败");
            isInit = false;
            return false;
        }
    }

    public void destory() {
        if (isInit) {
            TtsJni.JniDestory();
            isInit = false;
        }
    }

    /**
     * 获取程序文件存储目录，优先使用外部存储，再使用内部存储
     * @param context 上下文
     * @return 目录路径
     */
    public String getFilesPath(Context context) {
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


    private Handler mMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    break;
                case 2://播放完成
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 开启播报线程
     *
     * @param text
     */
    public synchronized void startReadThread(final String text) {
        class TtsRunThread implements Runnable {
            @Override
            public void run() {
                TtsJni.JniSpeak(text);
                int msgType = TtsJni.JniIsPlaying();
                Message s_msg = mMsgHandler.obtainMessage(msgType);
                mMsgHandler.sendMessageDelayed(s_msg, 0);
            }
        }
        Thread ttsRun = (new Thread(new TtsRunThread()));
        ttsRun.setPriority(Thread.MAX_PRIORITY);
        ttsRun.start();
    }
}
