package com.lois.tytool;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lois.tytool.device.SystemUtils;

/**
 * @Description TyTool入口
 * @Author Lois
 * @Date 2022/1/21 17:44
 */
public class TyTool {
    private static final String TAG = TyTool.class.getSimpleName();

    private Context mContext;
    private String mAppDir;

    public static TyTool getInstance() {
        return TyToolHolder.sInstance;
    }

    private static class TyToolHolder {
        private static final TyTool sInstance = new TyTool();
    }

    /**
     * 初始化TyTool工具，必须在Application的onCreate方法中调用
     * @param context 上下文
     * @return 对象本身
     */
    public TyTool init(@NonNull Context context) {
        if (context == null) {
            throw new RuntimeException("Context不能为空！");
        }
        if (isInit()) {
            return this;
        }
        this.mContext = context.getApplicationContext();
        this.mAppDir = SystemUtils.getAppFilePath(mContext) + "/";
        if (mAppDir == null) {
            throw new RuntimeException("TyTool初始化失败！");
        }
        return this;
    }

    public Context getContext() {
        if (mContext == null) {
            throw new RuntimeException("请先初始化TyTool。");
        }
        return mContext;
    }

    public String getAppDir() {
        if (mAppDir == null) {
            throw new RuntimeException("请先初始化TyTool。");
        }
        return mAppDir;
    }

    /**
     * 检查TyTool是否初始化
     */
    private void check() {
        getContext();
        getAppDir();
    }

    /**
     * 判断TyTool是否初始化
     *
     * @return true：是  false：否
     */
    public boolean isInit() {
        if (mContext == null || mAppDir == null) {
            return false;
        }
        return true;
    }

    /**
     * 开启异常捕获，必须在Application的onCreate方法中调用
     *
     * @return 对象本身
     */
    public TyTool openCrashHelper() {
        check();
        TyCrashHelper.install(mContext);
        return this;
    }

    /**
     * @param saveLevel 保存日志等级
     *                  DEFAULT_ALL = 1                      -->     保存所有等级日志
     *                  VERBOSE = 2;                         -->     保存VERBOSE及以上等级日志
     *                  DEBUG = 3;                           -->     保存DEBUG及以上等级日志
     *                  INFO = 4;                            -->     保存INFO及以上等级日志
     *                  WARN = 5;                            -->     保存WARN及以上等级日志
     *                  ERROR = 6;                           -->     保存ERROR及以上等级日志
     *                  ASSERT = 7;                          -->     保存ASSERT及以上等级日志
     *                  DEFAULT_NONE = Integer.MAX_VALUE;    -->     不保存日志
     * @return 对象本身
     */
    public TyTool setLogSaveInfo(@TyLog.LogLevel int saveLevel) {
        check();
        boolean ret = TyLog.setLogSaveInfo(this.mAppDir, saveLevel);
        if (!ret) {
            TyLog.w(TAG, "日志保存开启失败");
        }
        return this;
    }

}
