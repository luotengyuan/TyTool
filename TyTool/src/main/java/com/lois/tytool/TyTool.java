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

    private Context mContext;
    private String mAppDir;

    public static TyTool getInstance() {
        return TyToolHolder.sInstance;
    }

    private static class TyToolHolder {
        private static final TyTool sInstance = new TyTool();
    }

    public TyTool init(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        this.mAppDir = SystemUtils.getAppFilePath(mContext)+ "/";
        TyLog.setLogSaveInfo(this.mAppDir, com.lois.tytool.basej.debug.TyLog.DEFAULT_ALL);
//        TyCrashHelper.install(mContext);
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
}
