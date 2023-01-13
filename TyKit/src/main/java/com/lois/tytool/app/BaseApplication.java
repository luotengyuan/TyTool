package com.lois.tytool.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.lois.tytool.TyTool;

/**
 * @author Administrator
 */
public class BaseApplication extends Application {
    /**
     * 全局的APP上下文
     */
    protected static Context appContext;
    /**
     * 全局的Handler
     */
    protected static Handler handler;
    /**
     * 主线程的ID
     */
    protected static int mainThreadId;
    /**
     * 程序启动时间 单位：纳秒
     */
    protected static long appStartUpTime;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        handler = new Handler();
        mainThreadId = android.os.Process.myTid();
        appStartUpTime = System.nanoTime();
        TyTool.getInstance().init(this);
    }


    /**
     * 获取上下文对象
     *
     * @return context
     */
    public static Context getAppContext() {
        return appContext;
    }

    /**
     * 获取全局handler
     *
     * @return 全局handler
     */
    public static Handler getHandler() {
        return handler;
    }

    /**
     * 获取主线程id
     *
     * @return 主线程id
     */
    public static int getMainThreadId() {
        return mainThreadId;
    }

    /**
     * 程序启动时间 单位：纳秒
     *
     * @return 主线程id
     */
    public static long getAppStartUpTime() {
        return appStartUpTime;
    }

    /**
     * 程序运行时间 单位：纳秒
     *
     * @return 主线程id
     */
    public static long getAppRunTime() {
        return System.nanoTime() - appStartUpTime;
    }

}
