package com.lois.tytool.demo;

import android.content.Context;

import com.lois.tytool.TyTool;
import com.lois.tytool.app.BaseApplication;
import com.lois.tytool.base.debug.TyLog;

/**
 * @Description MyApplication
 * @Author Luo.T.Y
 * @Date 2022/2/11
 * @Time 9:02
 */
public class MyApp extends BaseApplication {
    private static final String TAG = MyApp.class.getSimpleName();

    /**
     * Global application context.
     */
    private static Context mApplicationContext;


    /**
     * Construct, initialize application context.
     */
    public MyApp() {
        mApplicationContext = this;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        TyTool.getInstance()
                .init(this)
//                .openCrashHelper()
                .setLogSaveInfo(TyLog.INFO);
    }

    /**
     * Get the global application context.
     */
    public static Context getGlobalContext() {
        if (mApplicationContext == null) {
            throw new RuntimeException("Application context is null");
        }
        return mApplicationContext;
    }
}
