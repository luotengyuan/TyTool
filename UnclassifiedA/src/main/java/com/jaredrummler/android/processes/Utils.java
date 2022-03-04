package com.jaredrummler.android.processes;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2015/3/24 0024.
 */
public class Utils {

    private static Utils utils;
    public static Utils getInst(){
        if (utils == null){
            utils = new Utils();
        }
        return utils;
    }
    public int ii = 1;

    /***
     * 服务是否正在运行
     * @param context
     * @param service 服务的名字
     * @return true 服务正在运行，false 服务未运行
     */
    public static boolean isServiceRunning(Context context, String service) {
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningServiceInfos =
                (ArrayList<ActivityManager.RunningServiceInfo>)
                        activityManager.getRunningServices(Integer.MAX_VALUE);
        for (int i=0; i<runningServiceInfos.size(); i++) {
            if (runningServiceInfos.get(i).service.getClassName().toString()
                    .equals(service)){
                return true;
            }
        }
        return false;
    }

    /**
     * 进程是否运行(在5.0之前可用这个方法)
     * @param context
     * @param proessName
     * @return
     */
    @Deprecated
    public static boolean isProessRunning(Context context, String proessName) {

        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        if (lists != null) {
            for (ActivityManager.RunningAppProcessInfo info : lists) {
                Log.v("TAG", "process " + info.processName);
                if (info.processName.equals(proessName)) {
                    Log.v("TAG", "process: " + info.processName);
                    isRunning = true;
                }
            }
        }

        return isRunning;
    }

    /**
     * 判断进程是否在运行（Android5.1以后接口被禁用，在https://github.com/jaredrummler/AndroidProcesses有不需要权限就可以获取运行程序的开源库）
     * @param proessName
     * @return
     */
    public static boolean isProessRunning(String proessName) {
        // Get a list of running apps
        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();

        for (AndroidAppProcess process : processes) {

            // Get some information about the process
            String processName = process.name;
            if (process.name.equals(proessName)) {
                Log.v("TAG", "process: " + process.name);
                return true;
            }
        }
        return false;
    }

    public static void restartService(Context context, String pakcename, String servicename){
        ComponentName componentName = new ComponentName(pakcename, servicename);
        try{
            Intent intent = new Intent();
            intent.setComponent(componentName);
            context.startService(intent);
        } catch (Exception e) {
            Log.v("TAG", e.toString());
        }
    }
}
