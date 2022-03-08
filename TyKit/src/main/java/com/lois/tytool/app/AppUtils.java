package com.lois.tytool.app;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.lois.tytool.TyTool;
import com.lois.tytool.base.io.FileUtils;
import com.lois.tytool.base.string.StringUtils;
import com.lois.tytool.process.ProcessUtils;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.x500.X500Principal;

/**
 * @Description App相关工具类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class AppUtils {

    /**
     * 获取上下文对象
     *
     * @return 上下文对象
     */
    public static Context getAppContext() {
        return BaseApplication.getAppContext();
    }

    /**
     * 获取全局handler
     *
     * @return 全局handler
     */
    public static Handler getHandler() {
        return BaseApplication.getHandler();
    }

    /**
     * 获取主线程id
     *
     * @return 主线程id
     */
    public static int getMainThreadId() {
        return BaseApplication.getMainThreadId();
    }

    /**
     * 获取程序包名
     *
     * @param context context
     * @return 程序包名
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * 判断是否运行在主线程
     *
     * @return true：当前线程运行在主线程
     * fasle：当前线程没有运行在主线程
     */
    public static boolean isRunOnUIThread() {
        // 获取当前线程id, 如果当前线程id和主线程id相同, 那么当前就是主线程
        int myTid = android.os.Process.myTid();
        if (myTid == getMainThreadId()) {
            return true;
        }
        return false;
    }

    /**
     * 运行在主线程
     *
     * @param r 运行的Runnable对象
     */
    public static void runOnUIThread(Runnable r) {
        if (isRunOnUIThread()) {
            // 已经是主线程, 直接运行
            r.run();
        } else {
            // 如果是子线程, 借助handler让其运行在主线程
            getHandler().post(r);
        }
    }

    /**
     * 通知是否可用
     */
    public static boolean areNotificationEnabled(Context context) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        return manager.areNotificationsEnabled();
    }

    /**
     * Get the application class name of current App.
     *
     * @return the application name
     */
    public static String getApplicationName(Context context) {
        return getApplicationName(context);
    }

    /**
     * Get the application class name of current App.
     *
     * @return the application name
     */
    public static String getApplicationName(Context context, String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai == null ? null : ai.className;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取APP名称
     *
     * @param context 上下文
     * @return APP名称
     */
    public static String getAppName(Context context) {
        return getAppName(context, context.getPackageName());
    }

    /**
     * 获取APP名称
     *
     * @param context     上下文
     * @param packageName 包名
     * @return APP名称
     */
    public static String getAppName(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        String appName = null;
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            appName = String.valueOf(pm.getApplicationLabel(applicationInfo));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

    /**
     * 获取APP图标
     *
     * @param context 上下文
     * @return APP图标
     */
    public static Drawable getAppIcon(Context context) {
        return getAppIcon(context, context.getPackageName());
    }

    /**
     * 获取APP图标
     *
     * @param context     上下文
     * @param packageName 包名
     * @return APP图标
     */
    public static Drawable getAppIcon(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        Drawable appIcon = null;
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            appIcon = applicationInfo.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appIcon;
    }

    /**
     * 获取APP Launcher activity名称
     *
     * @param context 上下文
     * @return APP Launcher activity名称
     */
    public static String getAppLauncher(Context context) {
        return getAppLauncher(context, context.getPackageName());
    }

    /**
     * 获取APP Launcher activity名称
     *
     * @param context 上下文
     * @param packageName 包名
     * @return APP Launcher activity名称
     */
    public static String getAppLauncher(Context context, String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            if (pi == null) {
                return null;
            }
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(pi.packageName);
            List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(resolveIntent, 0);
            ResolveInfo resolveInfo = resolveInfoList.iterator().next();
            if (resolveInfo != null) {
                return resolveInfo.activityInfo.name;
            }
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取APP首次安装时间
     *
     * @param context 上下文
     * @return APP首次安装时间
     */
    public static long getAppFirstInstallTime(Context context) {
        return getAppFirstInstallTime(context, context.getPackageName());
    }

    /**
     * 获取APP首次安装时间
     *
     * @param context     上下文
     * @param packageName 包名
     * @return APP首次安装时间
     */
    public static long getAppFirstInstallTime(Context context, String packageName) {
        long lastUpdateTime = 0;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            lastUpdateTime = packageInfo.firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return lastUpdateTime;
    }

    /**
     * 获取APP最后一次更新时间
     *
     * @param context 上下文
     * @return APP最后一次更新时间
     */
    public static long getAppLastUpdateTime(Context context) {
        return getAppLastUpdateTime(context, context.getPackageName());
    }

    /**
     * 获取APP最后一次更新时间
     *
     * @param context     上下文
     * @param packageName 包名
     * @return APP最后一次更新时间
     */
    public static long getAppLastUpdateTime(Context context, String packageName) {
        long lastUpdateTime = 0;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            lastUpdateTime = packageInfo.lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return lastUpdateTime;
    }

    /**
     * 获取APP安装包大小
     *
     * @param context 上下文
     * @return APP安装包大小
     */
    public static long getAppSize(Context context) {
        return getAppSize(context, context.getPackageName());
    }

    /**
     * 获取APP安装包大小
     *
     * @param context     上下文
     * @param packageName 包名
     * @return APP安装包大小
     */
    public static long getAppSize(Context context, String packageName) {
        long appSize = 0;
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            appSize = new File(applicationInfo.sourceDir).length();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appSize;
    }

    /**
     * 获取APP安装包路径
     *
     * @param context 上下文
     * @return APP安装包路径
     */
    public static String getAppApk(Context context) {
        return getAppApk(context, context.getPackageName());
    }

    /**
     * 获取APP安装包路径
     *
     * @param context     上下文
     * @param packageName 包名
     * @return APP安装包路径
     */
    public static String getAppApk(Context context, String packageName) {
        String sourceDir = null;
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            sourceDir = applicationInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return sourceDir;
    }

    /**
     * 获取APP版本名称
     *
     * @param context 上下文
     * @return APP版本名
     */
    public static String getAppVersionName(Context context) {
        return getAppVersionName(context, context.getPackageName());
    }

    /**
     * 获取APP版本名称
     *
     * @param context     上下文
     * @param packageName 包名
     * @return APP版本名
     */
    public static String getAppVersionName(Context context, String packageName) {
        String appVersion = null;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            appVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersion;
    }

    /**
     * 获取APP版本号
     *
     * @param context 上下文
     * @return APP版本号
     */
    public static int getAppVersionCode(Context context) {
        return getAppVersionCode(context, context.getPackageName());
    }

    /**
     * 获取APP版本号
     *
     * @param context     上下文
     * @param packageName 包名
     * @return APP版本号
     */
    public static int getAppVersionCode(Context context, String packageName) {
        int appVersionCode = 0;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            appVersionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersionCode;
    }

    /**
     * 获取系统版本号
     */
    public static String getSystemVersion() {
        String sysInfo = "";
        try {
            Class<?> build = Class.forName("android.os.Build");
            Field fi = build.getField("ODM_SW_VERSION");
            Object ob = fi.get(build.newInstance());
            sysInfo = ob.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sysInfo;
    }

    /**
     * 获取APP签名
     *
     * @param context 上下文
     * @return APP签名
     */
    public static String getAppSign(Context context) {
        return getAppSign(context, context.getPackageName());
    }

    /**
     * 获取APP签名
     *
     * @param context     上下文
     * @param packageName 包名
     * @return APP签名
     */
    public static String getAppSign(Context context, String packageName) {
        try {
            PackageInfo pis = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return StringUtils.bytesToHexString(pis.signatures[0].toByteArray());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Signature[] getAppSignature() {
        return getAppSignature(TyTool.getInstance().getContext().getPackageName());
    }

    public static Signature[] getAppSignature(String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            PackageManager pm = TyTool.getInstance().getContext().getPackageManager();
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return pi == null ? null : pi.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getAppSignatureSHA1() {
        return getAppSignatureSHA1(TyTool.getInstance().getContext().getPackageName());
    }

    public static String getAppSignatureSHA1(String packageName) {
        return getAppSignatureHash(packageName, "SHA1");
    }

    public static String getAppSignatureSHA256() {
        return getAppSignatureSHA256(TyTool.getInstance().getContext().getPackageName());
    }

    public static String getAppSignatureSHA256(String packageName) {
        return getAppSignatureHash(packageName, "SHA256");
    }

    public static String getAppSignatureMD5() {
        return getAppSignatureMD5(TyTool.getInstance().getContext().getPackageName());
    }

    public static String getAppSignatureMD5(String packageName) {
        return getAppSignatureHash(packageName, "MD5");
    }

    private static String getAppSignatureHash(String packageName, final String algorithm) {
        if (StringUtils.isEmpty(packageName)) {
            return "";
        }
        Signature[] signature = getAppSignature(packageName);
        if (signature == null || signature.length <= 0) {
            return "";
        }
        return StringUtils.bytesToHexString(hashTemplate(signature[0].toByteArray(), algorithm))
                .replaceAll("(?<=[0-9A-F]{2})[0-9A-F]{2}", ":$0");
    }

    private static byte[] hashTemplate(final byte[] data, final String algorithm) {
        if (data == null || data.length <= 0) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * 获取APP目标版本
     *
     * @param context 上下文
     * @return APP目标版本
     */
    public static int getAppTargetSdkVersion(Context context) {
        return getAppTargetSdkVersion(context, context.getPackageName());
    }

    /**
     * 获取APP目标版本
     *
     * @param context     上下文
     * @param packageName 包名
     * @return APP目标版本
     */
    public static int getAppTargetSdkVersion(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            return applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取APP最低适配版本
     *
     * @param context     上下文
     * @return APP最低适配版本
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int getAppMinSdkVersion(Context context) {
        return getAppMinSdkVersion(context, context.getPackageName());
    }

    /**
     * 获取APP最低适配版本
     *
     * @param context     上下文
     * @param packageName 包名
     * @return APP最低适配版本
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int getAppMinSdkVersion(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai == null ? 0 : ai.minSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取指定Manifest文件下Application等节点下的metadata自定义数据
     *
     * @param context     上下文
     * @return 指定metadata数据
     */
    public static String getMetaData(Context context, String key) {
        return getMetaData(context, context.getPackageName(), key);
    }


    /**
     * 获取指定Manifest文件下Application等节点下的metadata自定义数据
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 指定metadata数据
     */
    public static String getMetaData(Context context, String packageName, String key) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return ai != null && ai.metaData != null ? ai.metaData.getString(key) : null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取APP UID
     *
     * @param context 上下文
     * @return APP UID
     */
    public static int getAppUid(Context context) {
        return getAppUid(context, context.getPackageName());
    }

    /**
     * 获取APP UID
     *
     * @param context     上下文
     * @param packageName 包名
     * @return APP UID
     */
    public static int getAppUid(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            return applicationInfo.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取APP是否拥有系统权限
     *
     * @param context 上下文
     * @return true：是  false：否
     */
    public static boolean getRootPermission(Context context) {
        String packageCodePath = context.getPackageCodePath();
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + packageCodePath;
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 获取APP权限列表
     *
     * @param context 上下文
     * @return 权限列表
     */
    public static String[] getAppPermissions(Context context) {
        return getAppPermissions(context, context.getPackageName());
    }

    /**
     * 获取APP权限列表
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 权限列表
     */
    public static String[] getAppPermissions(Context context, String packageName) {
        String[] requestedPermissions = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            requestedPermissions = info.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return requestedPermissions;
    }

    /**
     * 判断APP是否拥有某项权限
     *
     * @param context    上下文
     * @param permission 权限
     * @return true：是  false：否
     */
    public static boolean hasPermission(Context context, String permission) {
        if (context != null && !TextUtils.isEmpty(permission)) {
            try {
                PackageManager packageManager = context.getPackageManager();
                if (packageManager != null) {
                    if (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(permission, context
                            .getPackageName())) {
                        return true;
                    }
                    Log.d("AppUtils", "Have you  declared permission " + permission + " in AndroidManifest.xml ?");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 判断APP是否已安装
     *
     * @param context     上下文
     * @param packageName 包名
     * @return true：是  false：否
     */
    public static boolean isInstalled(Context context, String packageName) {
        boolean installed = false;
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        List<ApplicationInfo> installedApplications = context.getPackageManager().getInstalledApplications(0);
        for (ApplicationInfo in : installedApplications) {
            if (packageName.equals(in.packageName)) {
                installed = true;
                break;
            } else {
                installed = false;
            }
        }
        return installed;
    }

    /**
     * 安装文件
     *
     * @param context 上下文
     * @param data    数据
     */
    public static boolean installApk(Context context, Uri data) {
        Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(data, "application/vnd.android.package-archive");
        // FLAG_ACTIVITY_NEW_TASK 可以保证安装成功时可以正常打开 app
        promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(promptInstall);
        return true;
    }

    /**
     * 通过系统界面安装apk
     *
     * @param context  上下文
     * @param filePath apk路径
     * @return true：安装成功  false：安装失败
     */
    @Deprecated
    public static boolean installApk(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile() || file.length() <= 0) {
            return false;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return true;
    }

    /**
     * 静默安装（需要root权限）
     *
     * @param path 安装路径
     * @return true：成功   false：失败
     */
    public static boolean installSilent(String path) {
        boolean result;
        String str = ProcessUtils.execute("pm", "install", "-r", path);
        if (str != null && (str.contains("Success") || str.contains("success"))) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * 静默安装并启动应用（需要root权限）
     *
     * @param path        安装路径
     * @param packageName 包名
     * @return true：成功   false：失败
     */
    public static boolean installAndStartSilent(Context context, String path, String packageName) {
        boolean result;
        String str = ProcessUtils.execute("pm", "install", "-r", path);
        if (str == null) {
            return false;
        }
        List<ResolveInfo> matches = findActivitiesForPackage(context, packageName);
        if ((matches != null) && (matches.size() > 0)) {
            ResolveInfo resolveInfo = matches.get(0);
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            String result1 = ProcessUtils.execute("am", "start", "-n", packageName + "/" + activityInfo.name);
            int t1 = findTimes("Error", packageName + "/" + activityInfo.name) + findTimes("error", packageName + "/" + activityInfo.name);
            int t2 = findTimes("Error", result1) + findTimes("error", result1);
        }
        if (str != null && (str.contains("Success") || str.contains("success"))) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * 判断字符串s1在字符串s2中出现的次数
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 出现次数
     */
    private static int findTimes(String str1, String str2) {
        int total = 0;
        if (str1 != null && !str1.equals("") && str2 != null && !str2.equals("")) {
            for (String tmp = str2; tmp != null && tmp.length() >= str1.length(); ) {
                int idx = tmp.indexOf(str1);
                if (idx != -1) {
                    total++;
                    tmp = tmp.substring(idx + str1.length(), tmp.length());
                } else {
                    return total;
                }
            }
        }
        return total;
    }

    /**
     * 通过包名获取Activities
     *
     * @param context     上下文
     * @param packageName 包名
     * @return Activities
     */
    public static List<ResolveInfo> findActivitiesForPackage(Context context, String packageName) {
        final PackageManager pm = context.getPackageManager();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(packageName);

        final List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
        return apps != null ? apps : new ArrayList<ResolveInfo>();
    }

    /**
     * 通过包名静默卸载apk（需要root权限）
     *
     * @param packageName 包名
     * @return true：成功   false：失败
     */
    public static boolean uninstallAPKSilent(String packageName) {
        boolean result;
        String str = ProcessUtils.execute("pm", "uninstall", packageName);
        if (str != null && (str.contains("Success") || str.contains("success"))) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * 通过系统界面卸载app
     *
     * @param context     上下文
     * @param packageName 包名
     * @return true：卸载成功  false：卸载失败
     */
    @Deprecated
    public static boolean uninstallApk(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        Intent i = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + packageName));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return true;
    }

    /**
     * 判断是否系统APP
     *
     * @param context 上下文
     * @return true：是  false：否
     */
    public static boolean isSystemApp(Context context) {
        return isSystemApp(context, context.getPackageName());
    }

    /**
     * 判断是否系统APP
     *
     * @param context     上下文
     * @param packageName 包名
     * @return true：是  false：否
     */
    public static boolean isSystemApp(Context context, String packageName) {
        boolean isSys = false;
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            if (applicationInfo != null && (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                isSys = true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            isSys = false;
        }
        return isSys;
    }

    /**
     * 判断服务是否允许
     *
     * @param context   上下文
     * @param className 类名
     * @return true：是  false：否
     */
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo si : servicesList) {
            if (className.equals(si.service.getClassName())) {
                isRunning = true;
            }
        }
        return isRunning;
    }

    /**
     * 停止指定类名服务
     *
     * @param context   上下文
     * @param className 类名
     * @return true：是  false：否
     */
    public static boolean stopRunningService(Context context, String className) {
        Intent intent_service = null;
        boolean ret = false;
        try {
            intent_service = new Intent(context, Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (intent_service != null) {
            ret = context.stopService(intent_service);
        }
        return ret;
    }

    /**
     * whether application is in background
     * <ul>
     * <li>need use permission android.permission.GET_TASKS in Manifest.xml</li>
     * </ul>
     *
     * @param context 上下文
     * @return if application is in background return true, otherwise return
     * false
     */
    public static boolean isAppInBackground(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            if (topActivity != null
                    && !topActivity.getPackageName().equals(
                    context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过包名启动app
     *
     * @param context     上下文
     * @param packagename 包名
     */
    public static void runApp(Context context, String packagename) {
        context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage(packagename)));
    }

    public static String getAppSourceDir() {
        return getAppSourceDir(TyTool.getInstance().getContext().getPackageName());
    }

    public static String getAppSourceDir(String packageName) {
        try {
            PackageManager pm = TyTool.getInstance().getContext().getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai == null ? null : ai.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 清除APP缓存
     *
     * @param context
     */
    @Deprecated
    public static void cleanCache(Context context) {
        FileUtils.deleteFileByDirectory(context.getCacheDir());
    }

    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)
     *
     * @param context 上下文
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases)
     *
     * @param context 上下文
     */
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File(context.getFilesDir().getPath() + context.getPackageName() + "/databases"));
    }

    /**
     * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs)
     *
     * @param context 上下文
     */
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File(context.getFilesDir().getPath() + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * 按名字清除本应用数据库
     *
     * @param context 上下文
     * @param dbName  数据库名称
     */
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容
     *
     * @param context 上下文
     */
    public static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }

    /**
     * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     *
     * @param context 上下文
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除
     *
     * @param filePath 文件路径
     */
    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }

    /**
     * 清除本应用所有的数据
     *
     * @param context  上下文
     * @param filePath 文件路径
     */
    public static void cleanApplicationData(Context context, String... filePath) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
        for (String fp : filePath) {
            cleanCustomCache(fp);
        }
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件。 此操作较危险，请慎用；
     *
     * @param directory 文件夹File对象
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                // delete file and folder. It's dangerous!
                if (item.isDirectory()) {
                    deleteFilesByDirectory(item);
                } else {
                    item.delete();
                }
            }
        }
    }

    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    private static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static String getCacheSize(File file) throws Exception {
        return getFormatSize(getFolderSize(file));
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    /**
     * 获取系统中所有的应用
     *
     * @param context 上下文
     * @return 应用信息List
     */
    public static List<PackageInfo> getAllApps(Context context) {

        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = paklist.get(i);
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                apps.add(pak);
            }
        }
        return apps;
    }

    private final static X500Principal DEBUG_DN = new X500Principal(
            "CN=Android Debug,O=Android,C=US");

    /**
     * 检测当前应用是否是Debug版本
     *
     * @param ctx
     * @return
     */
    public static boolean isDebuggable(Context ctx) {
        boolean debuggable = false;
        try {
            PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature signatures[] = pinfo.signatures;
            for (int i = 0; i < signatures.length; i++) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                ByteArrayInputStream stream = new ByteArrayInputStream(signatures[i].toByteArray());
                X509Certificate cert = (X509Certificate) cf
                        .generateCertificate(stream);
                debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
                if (debuggable)
                    break;
            }

        } catch (PackageManager.NameNotFoundException e) {
        } catch (CertificateException e) {
        }
        return debuggable;
    }

    /**
     * 是否是主线程
     *
     * @return
     */
    public static boolean isMainProcess(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
