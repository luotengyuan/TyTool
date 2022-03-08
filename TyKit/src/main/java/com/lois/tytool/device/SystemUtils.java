package com.lois.tytool.device;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.lois.tytool.TyTool;
import com.lois.tytool.base.io.IOUtils;
import com.lois.tytool.io.FileUtils;
import com.lois.tytool.process.ShellUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 设备及软件系统相关工具类
 * @Author Luo.T.Y
 * @Date 2022/2/23
 * @Time 15:14
 */
public class SystemUtils {
    private static final String TAG = SystemUtils.class.getSimpleName();
    private final static String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_FLYME_VERSION_NAME = "ro.build.display.id";
    private final static String FLYME = "flyme";
    private final static String ZTEC2016 = "zte c2016";
    private final static String ZUKZ1 = "zuk z1";
    private final static String ESSENTIAL = "essential";
    private final static String MEIZUBOARD[] = {"m9", "M9", "mx", "MX"};
    private static String sMiuiVersionName;
    private static String sFlymeVersionName;
    private static Boolean sIsTabletValue = null;
    private static final String BRAND = Build.BRAND.toLowerCase();

    static {
        Properties properties = new Properties();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // android 8.0，读取 /system/uild.prop 会报 permission denied
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
                properties.load(fileInputStream);
            } catch (Exception e) {
                Log.e(TAG, "read file error " + e);
            } finally {
                IOUtils.closeAll(fileInputStream);
            }
        }

        Class<?> clzSystemProperties = null;
        try {
            clzSystemProperties = Class.forName("android.os.SystemProperties");
            Method getMethod = clzSystemProperties.getDeclaredMethod("get", String.class);
            // miui
            sMiuiVersionName = getLowerCaseName(properties, getMethod, KEY_MIUI_VERSION_NAME);
            // flyme
            sFlymeVersionName = getLowerCaseName(properties, getMethod, KEY_FLYME_VERSION_NAME);
        } catch (Exception e) {
            Log.e(TAG, "read SystemProperties error " + e);
        }
    }

    /**
     * 判断是否为平板设备
     */
    public static boolean isTablet(Context context) {
        if (sIsTabletValue != null) {
            return sIsTabletValue;
        }
        sIsTabletValue = isTabletInner(context);
        return sIsTabletValue;
    }

    /**
     * 判断是否是flyme系统
     */
    public static boolean isFlyme() {
        return !TextUtils.isEmpty(sFlymeVersionName) && sFlymeVersionName.contains(FLYME);
    }

    /**
     * 判断是否是MIUI系统
     */
    public static boolean isMIUI() {
        return !TextUtils.isEmpty(sMiuiVersionName);
    }

    public static boolean isMIUIV5() {
        return "v5".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV6() {
        return "v6".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV7() {
        return "v7".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV8() {
        return "v8".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV9() {
        return "v9".equals(sMiuiVersionName);
    }

    public static boolean isFlymeVersionHigher5_2_4() {
        //查不到默认高于5.2.4
        boolean isHigher = true;
        if (sFlymeVersionName != null && !sFlymeVersionName.equals("")) {
            Pattern pattern = Pattern.compile("(\\d+\\.){2}\\d");
            Matcher matcher = pattern.matcher(sFlymeVersionName);
            if (matcher.find()) {
                String versionString = matcher.group();
                if (versionString != null && !versionString.equals("")) {
                    String[] version = versionString.split("\\.");
                    if (version.length == 3) {
                        if (Integer.valueOf(version[0]) < 5) {
                            isHigher = false;
                        } else if (Integer.valueOf(version[0]) > 5) {
                            isHigher = true;
                        } else {
                            if (Integer.valueOf(version[1]) < 2) {
                                isHigher = false;
                            } else if (Integer.valueOf(version[1]) > 2) {
                                isHigher = true;
                            } else {
                                if (Integer.valueOf(version[2]) < 4) {
                                    isHigher = false;
                                } else if (Integer.valueOf(version[2]) >= 5) {
                                    isHigher = true;
                                }
                            }
                        }
                    }

                }
            }
        }
        return isMeizu() && isHigher;
    }

    public static boolean isMeizu() {
        return isPhone() || isFlyme();
    }

    /**
     * 判断是否为小米
     * https://dev.mi.com/doc/?p=254
     */
    public static boolean isXiaomi() {
        return Build.MANUFACTURER.toLowerCase().equals("xiaomi");
    }

    public static boolean isVivo() {
        return BRAND.contains("vivo") || BRAND.contains("bbk");
    }

    public static boolean isOppo() {
        return BRAND.contains("oppo");
    }

    public static boolean isHuawei() {
        return BRAND.contains("huawei") || BRAND.contains("honor");
    }

    public static boolean isEssentialPhone(){
        return BRAND.contains("essential");
    }

    /**
     * 判断是否为 ZUK Z1 和 ZTK C2016。
     * 两台设备的系统虽然为 android 6.0，但不支持状态栏icon颜色改变，因此经常需要对它们进行额外判断。
     */
    public static boolean isZUKZ1() {
        final String board = android.os.Build.MODEL;
        return board != null && board.toLowerCase().contains(ZUKZ1);
    }

    public static boolean isZTKC2016() {
        final String board = android.os.Build.MODEL;
        return board != null && board.toLowerCase().contains(ZTEC2016);
    }

    public static boolean isPhone() {
        TelephonyManager tm =
                (TelephonyManager) TyTool.getInstance().getContext().getSystemService(Context.TELEPHONY_SERVICE);
        // noinspection ConstantConditions
        return tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isAdbEnabled() {
        return Settings.Secure.getInt(
                TyTool.getInstance().getContext().getContentResolver(),
                Settings.Global.ADB_ENABLED, 0
        ) > 0;
    }

    /**
     * 是否有SDCard
     *
     * @return 是否有SDCard
     */
    public static boolean hasSDCard() {

        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取应用运行的最大内存
     *
     * @return 最大内存
     */
    public static long getMaxMemory() {

        return Runtime.getRuntime().maxMemory() / 1024;
    }

    /**
     * 获取当前是否在打电话状态
     *
     * @return
     */
    public static boolean isCalling(Context context) {
        TelephonyManager telephonymanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        switch (telephonymanager.getCallState()) {
            case TelephonyManager.CALL_STATE_OFFHOOK: // 振铃
            case TelephonyManager.CALL_STATE_RINGING: // 摘机
                return true;
            default:
                return false;
        }
    }

    /**
     * 获取IMSI号
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
    }

    /**
     * 获取IMEI号
     *
     * @param context
     * @return 设备的IMEI号
     */
    public static String getIMEI(Context context) {
        String imei = null;
        if (imei == null && context != null) {
            TelephonyManager telmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telmgr.getDeviceId();
        }
        return imei;
    }

    public static String getAndroidID(Context ctx) {
        return Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * SD卡是否有效
     *
     * @return
     */

    public static boolean isSDCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取内置SD可用的存储空间
     *
     * @return 大小(MB)
     */
    public static float getSDCardSize() {
        String state = Environment.getExternalStorageState();
        // SD卡不可用
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return 0;
        }

        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        float size = (availableBlocks * blockSize) * 1.0f / (1024 * 1024);
        return size;
    }

    /**
     * 内置SD卡剩余空间是否够
     *
     * @param size
     * @return
     */
    public static boolean isSDCardSizeEnough(int size) {
        if (getSDCardSize() > size) {
            return true;
        }
        return false;
    }

    /**
     * 获取外置SD卡可用的存储空间
     *
     * @return 大小(MB)
     */
    public static float getExtSDCardSize() {
        String path = getExtSDCardPath();
        if (path == null) {
            return 0;
        }
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        float size = (availableBlocks * blockSize) * 1.0f / (1024 * 1024);
        return size;
    }

    /**
     * 外置SD卡剩余空间是否够
     *
     * @param size
     * @return
     */
    public static boolean isExtSDCardSizeEnough(int size) {
        if (getExtSDCardSize() > size) {
            return true;
        }
        return false;
    }

    /**
     * 获取可用的手机内部存储空间(ROM)
     *
     * @return 大小(MB)
     */
    public static float getAvailableInternalStorage() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        float size = (availableBlocks * blockSize) * 1.0f / (1024 * 1024);
        return size;
    }

    /**
     * 获取手机内部存储空间大小(ROM)
     *
     * @return 大小(MB)
     */
    public static float getTotalInternalStorage() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        float size = (totalBlocks * blockSize) * 1.0f / (1024 * 1024);
        return size;
    }

    /**
     * 获取可用的手机运行存储空间(RAM)
     *
     * @return 大小(MB)
     */
    public static float getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem * 1.0f / (1024 * 1024);
    }

    /**
     * 获取手机运行存储空间大小(RAM)
     *
     * @return 大小(MB)
     */
    public static float getTotalMemory(Context context) {
        // 系统内存信息文件
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            // 读取meminfo第一行，系统总内存大小
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");

            // 获得系统总内存, 单位是KB
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue();
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return initial_memory * 1.0f / 1024;
    }

    /**
     * 手机内部存储空间是否足够
     *
     * @param size
     * @return
     */
    public static boolean isInternalStorageEnough(int size) {
        if (getAvailableInternalStorage() > size) {
            return true;
        }
        return false;
    }

    /**
     * 存储空间是否足够,包括SD和手机内部Rom存储
     *
     * @param size 要求大小
     *             (MB)
     * @return
     */
    public static boolean isStorageEnough(int size) {
        if (getSDCardSize() > size || getExtSDCardSize() > size
                || getAvailableInternalStorage() > size) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前系统内存大小(MB)
     *
     * @return
     */
    public static long getAvailableMem(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo meminfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(meminfo);
        long num = meminfo.availMem / (1024 * 1024);
        return num;
    }

    /**
     * 获取可用的RAM内存空间 返回 单位 M 是否大于10M
     *
     * @param size 要求大小
     *             (MB)
     * @return boolean true:是false;否
     */
    public static boolean isMemEnough(int size, Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo meminfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(meminfo);

        long num = meminfo.availMem / (1024 * 1024);
        if (num < size) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * SIM卡是否有效
     *
     * @param context
     * @return
     */
    public static boolean isSIMCardAvailable(Context context) {
        if (((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimState() != TelephonyManager.SIM_STATE_ABSENT) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取外置SD路径
     *
     * @return
     */
    public static String getExtSDCardPath() {
        ArrayList<String> sdCardPathArrayList = getSDCardsPath();
        String sdCardPath = Environment.getExternalStorageDirectory().getPath();
        if (sdCardPathArrayList == null || sdCardPathArrayList.size() < 1) {
            return null;
        }
        for (String extSDCardPath : sdCardPathArrayList) {
            if (!extSDCardPath.equals(sdCardPath)) {
                return extSDCardPath;
            }
        }

        return null;
    }

    /**
     * 获取APP内置SD路径
     *
     * @return
     */
    public static String getAppFilePath(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath();
    }

    /**
     * 获取全部SD卡路径
     *
     * @return
     */
    private static ArrayList<String> getSDCardsPath() {
        ArrayList<String> mount = new ArrayList<String>();
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;

            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure")) {
                    continue;
                }
                if (line.contains("asec")) {
                    continue;
                }
                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        mount.add(columns[1]);
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        mount.add(columns[1]);
                    }
                }
            }
            is.close();
            isr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mount;
    }

    /**
     * 获取主板信息
     *
     * @return
     */
    public static String getBoard() {
        return Build.BOARD;
    }

    /**
     * 获取 android系统定制商
     *
     * @return
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 获取 cpu指令集
     *
     * @return
     */
    public static String getCpuAbi() {
        return Build.CPU_ABI;
    }

    /**
     * 获取设备参数
     *
     * @return
     */
    public static String getDevice() {
        return Build.DEVICE;
    }

    /**
     * 获取 硬件制造商
     *
     * @return
     */
    public static String getManufacture() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取 版本
     *
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取 硬件名称，包括平台版本号
     *
     * @return
     */
    public static String getFingerPrint() {
        return Build.FINGERPRINT;
    }

    /**
     * 获取 手机制造商即手机型号
     *
     * @return
     */
    public static String getProduct() {
        return Build.PRODUCT;
    }

    /**
     * 获取修订版本列表
     *
     * @return
     */
    public static String getID() {
        return Build.ID;
    }

    /**
     * 获取builder类型
     *
     * @return
     */
    public static String getType() {
        return Build.TYPE;
    }

    /**
     * 获取序号
     *
     * @return
     */
    public static String getSerial() {
        return Build.SERIAL;
    }

    public static String getHardware() {
        return Build.HARDWARE;
    }

    public static String getBuildHost() {
        return Build.HOST;
    }

    public static String getBuildTags() {
        return Build.TAGS;
    }

    public static long getBuildTime() {
        return Build.TIME;
    }

    public static String getBuildUser() {
        return Build.USER;
    }

    public static String getBuildVersionRelease() {
        return Build.VERSION.RELEASE;
    }

    public static String getBuildVersionCodename() {
        return Build.VERSION.CODENAME;
    }

    public static String getBuildVersionIncremental() {
        return Build.VERSION.INCREMENTAL;
    }

    public static int getBuildVersionSDK() {
        return Build.VERSION.SDK_INT;
    }

    public static String getBootloader() {
        return Build.BOOTLOADER;
    }

    public static String getRadioVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ? Build.getRadioVersion() : "";
    }

    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String getCountry(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        Locale locale = Locale.getDefault();
        return tm.getSimState() == TelephonyManager.SIM_STATE_READY ? tm.getSimCountryIso().toLowerCase(Locale.getDefault()) : locale.getCountry().toLowerCase(locale);
    }

    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    //<uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES"/>
    public static String getGSFID(Context context) {
        String result;
        final Uri URI = Uri.parse("content://com.google.android.gsf.gservices");
        final String ID_KEY = "android_id";
        String[] params = {ID_KEY};
        Cursor c = context.getContentResolver().query(URI, null, null, params, null);
        if (c == null || !c.moveToFirst() || c.getColumnCount() < 2) {
            return null;
        } else {
            result = Long.toHexString(Long.parseLong(c.getString(1)));
        }
        c.close();
        return result;
    }

    /**
     * 手机基本情况信息
     *
     * @return
     */
    public static String getDeviceInfoS(Context context) {
        StringBuffer sbDevice = new StringBuffer();
        // SD卡
        try {
            sbDevice.append("\nSD卡: ");
            sbDevice.append((int) getSDCardSize());
            sbDevice.append("M / ");
            sbDevice.append((int) getExtSDCardSize());
            sbDevice.append("M");
        } catch (Exception e) {
            Log.w(TAG, "未获得读取SD卡信息权限");
        }

        // ROM
        try {
            sbDevice.append("\n内部存储: ");
            sbDevice.append((int) getAvailableInternalStorage());
            sbDevice.append("M / ");
            sbDevice.append((int) getTotalInternalStorage());
            sbDevice.append("M");
        } catch (Exception e) {
            Log.w(TAG, "未获得读取内部存储信息权限");
        }

        // RAM
        try {
            sbDevice.append("\n运行内存: ");
            sbDevice.append((int) getAvailMemory(context));
            sbDevice.append("M / ");
            sbDevice.append((int) getTotalMemory(context));
            sbDevice.append("M");
        } catch (Exception e) {
            Log.w(TAG, "未获得读取运行内存信息权限");
        }
        Log.w(TAG, sbDevice.toString());
        return sbDevice.toString();
    }

    /**
     * 获取设备CPU核数
     *
     * @return CPU核数
     */
    public static int getNumCores() {
        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    return Pattern.matches("cpu[0-9]", pathname.getName());
                }

            });
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static String[] getSupportedABIS() {
        String[] result = new String[]{"-"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            result = Build.SUPPORTED_ABIS;
        }
        if (result == null || result.length == 0) {
            result = new String[]{"-"};
        }
        return result;
    }

    /**
     * 获取唯一ID
     * @return
     */
    public static String getPsuedoUniqueID() {
        String devIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            devIDShort += (Build.SUPPORTED_ABIS[0].length() % 10);
        } else {
            devIDShort += (Build.CPU_ABI.length() % 10);
        }
        devIDShort += (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
        String serial;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            return new UUID(devIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception e) {
            serial = "ESYDV000";
        }
        return new UUID(devIDShort.hashCode(), serial.hashCode()).toString();
    }

    public static String getUA(Context ctx) {
        final String system_ua = System.getProperty("http.agent");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return WebSettings.getDefaultUserAgent(ctx) + "__" + system_ua;
        } else {
            return new WebView(ctx).getSettings().getUserAgentString() + "__" + system_ua;
        }
    }

    public static void sendSMS(Context cxt, String smsBody) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", smsBody);
        cxt.startActivity(intent);
    }

    public static void forwardToDial(Activity activity, String phoneNumber) {
        if (activity != null && !TextUtils.isEmpty(phoneNumber)) {
            activity.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
        }
    }

    public static void sendMail(Context mContext, String mailID) {
        Uri uri = Uri.parse("mailto:" + mailID);
        mContext.startActivity(new Intent(Intent.ACTION_SENDTO, uri));
    }

    public static void openWeb(Context context, String url) {
        Uri uri = Uri.parse(url);
        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public static void openContacts(Activity context, int requestCode) {
        Uri uri = Uri.parse("content://contacts/people");
        context.startActivityForResult(new Intent(Intent.ACTION_PICK, uri), requestCode);
    }

    public static void openSettings(Activity context, String action) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.android.settings", action);
        intent.setComponent(comp);
        intent.setAction("android.intent.action.VIEW");
        context.startActivityForResult(intent, 0);
    }

    /**
     * Toggle keyboard If the keyboard is visible,then hidden it,if it's
     * invisible,then show it
     *
     * @param context 上下文
     */
    public static void toggleKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showKeyBoard(Context mContext, EditText mEditText){
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void hideKeyBoard(Context mContext, EditText mEditText){
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void hideKeyBoard(Activity aty){
        InputMethodManager imm = (InputMethodManager) aty.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(aty.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
            }
        }
        return false;
    }

    public static boolean isSleeping(Context context) {
        KeyguardManager kgMgr = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return kgMgr.inKeyguardRestrictedInputMode();
    }

    private static final String suSearchPaths[] = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};

    public static boolean isRooted() {
        File file;
        boolean flag1 = false;
        for (String suSearchPath : suSearchPaths) {
            file = new File(suSearchPath + "su");
            if (file.isFile() && file.exists()) {
                flag1 = true;
                break;
            }
        }
        return flag1;
    }

    public static boolean isRunningOnEmulator() {
        return Build.BRAND.contains("generic")
                || Build.DEVICE.contains("generic")
                || Build.PRODUCT.contains("sdk")
                || Build.HARDWARE.contains("goldfish")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("vbox86p")
                || Build.DEVICE.contains("vbox86p")
                || Build.HARDWARE.contains("vbox86");
    }

    public static void goHome(Context context) {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(mHomeIntent);
    }

    public static int gc(Context cxt) {
        //long i = getDeviceUsableMemory(cxt);
        int count = 0;
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(100);
        if (serviceList != null) {
            for (ActivityManager.RunningServiceInfo service : serviceList) {
                if (service.pid == android.os.Process.myPid()) {
                    continue;
                }
                try {
                    android.os.Process.killProcess(service.pid);
                    count++;
                } catch (Exception e) {
                    e.getStackTrace();
                    //continue;
                }
            }
        }

        List<ActivityManager.RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        if (processList != null) {
            for (ActivityManager.RunningAppProcessInfo process : processList) {
                if (process.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    String[] pkgList = process.pkgList;
                    for (String pkgName : pkgList) {
                        try {
                            am.killBackgroundProcesses(pkgName);
                            count++;
                        } catch (Exception e) {
                            e.getStackTrace();
                            //continue;
                        }
                    }
                }
            }
        }
        return count;
    }

    public static String getProcessName(Context appContext) {
        String currentProcessName = null;
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                currentProcessName = processInfo.processName;
                break;
            }
        }
        return currentProcessName;
    }

    public static void createDeskShortCut(Context cxt, String shortCutName, int icon, Class<?> cls) {
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutIntent.putExtra("duplicate", false);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortCutName);
        Parcelable ico = Intent.ShortcutIconResource.fromContext(cxt.getApplicationContext(), icon);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, ico);
        Intent intent = new Intent(cxt, cls);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        cxt.sendBroadcast(shortcutIntent);
    }

    public static void createShortcut(Context ctx, String shortCutName, int iconId, Intent presentIntent) {
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutIntent.putExtra("duplicate", false);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortCutName);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(ctx, iconId));
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, presentIntent);
        ctx.sendBroadcast(shortcutIntent);
    }

    public static void shareText(Context ctx, String title, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        ctx.startActivity(Intent.createChooser(intent, title));
       /* List<ResolveInfo> ris = getShareTargets(ctx);
        if (ris != null && ris.size() > 0) {
            ctx.startActivity(Intent.createChooser(intent, title));
        }*/
    }

    public static void shareFile(Context ctx, String title, String filePath) {
        FileUtils.shareFile(ctx, title, filePath);
    }


    @SuppressLint("WrongConstant")
    public static List<ResolveInfo> getShareTargets(Context ctx) {
        Intent intent = new Intent(Intent.ACTION_SEND, null);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        PackageManager pm = ctx.getPackageManager();
        return pm.queryIntentActivities(intent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
    }

    public static String getCurrentLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String getLanguage(Context ctx) {
        if (ctx != null) {
            return ctx.getResources().getConfiguration().locale.getLanguage();
        }
        return null;
    }

    //<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void showSoftInputMethod(Context context, EditText editText) {
        if (context != null && editText != null) {
            editText.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(editText, 0);
        }
    }

    public static void closeSoftInputMethod(Context context, EditText editText) {
        if (context != null && editText != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        }
    }

    public static void showSoftInput(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void closeSoftInput(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && ((Activity) context).getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void toWeChatScan(Context context) throws RuntimeException {
        try {
            Uri uri = Uri.parse("weixin://dl/scan");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } catch (Exception e) {
            throw new RuntimeException("无法跳转到微信，请检查您是否安装了微信!");
        }
    }

    public static void toAliPayScan(Context context) throws RuntimeException {
        try {
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } catch (Exception e) {
            throw new RuntimeException("无法跳转到支付宝，请检查您是否安装了支付宝!");
        }
    }

    public static void toAliPayPayCode(Context context) throws RuntimeException {
        try {
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=20000056");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } catch (Exception e) {
            throw new RuntimeException("无法跳转到支付宝，请检查您是否安装了支付宝!");
        }
    }

    /**
     * 显示软键盘
     */
    public static void openSoftInput(EditText et) {
        InputMethodManager inputMethodManager = (InputMethodManager) et.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(et, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftInput(EditText et) {
        InputMethodManager inputMethodManager = (InputMethodManager) et.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), InputMethodManager
                .HIDE_NOT_ALWAYS);
    }

    /**
     * 将内容复制到剪切板
     * @param context 上下文
     * @param text 内容
     */
    public static void copyToClipboard(Context context, String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context
                .CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("clip", text);
        cm.setPrimaryClip(clip);
    }

    /**
     * 是否Dalvik模式
     *
     * @return 结果
     */
    public static boolean isDalvik() {
        return "Dalvik".equals(getCurrentRuntimeValue());
    }

    /**
     * 是否ART模式
     *
     * @return 结果
     */
    public static boolean isART() {
        String currentRuntime = getCurrentRuntimeValue();
        return "ART".equals(currentRuntime) || "ART debug build".equals(currentRuntime);
    }

    /**
     * 获取手机当前的Runtime
     *
     * @return 正常情况下可能取值Dalvik, ART, ART debug build;
     */
    public static String getCurrentRuntimeValue() {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            try {
                Method get = systemProperties.getMethod("get",
                        String.class, String.class);
                if (get == null) {
                    return "WTF?!";
                }
                try {
                    final String value = (String) get.invoke(
                            systemProperties, "persist.sys.dalvik.vm.lib",
                            /* Assuming default is */"Dalvik");
                    if ("libdvm.so".equals(value)) {
                        return "Dalvik";
                    } else if ("libart.so".equals(value)) {
                        return "ART";
                    } else if ("libartd.so".equals(value)) {
                        return "ART debug build";
                    }

                    return value;
                } catch (IllegalAccessException e) {
                    return "IllegalAccessException";
                } catch (IllegalArgumentException e) {
                    return "IllegalArgumentException";
                } catch (InvocationTargetException e) {
                    return "InvocationTargetException";
                }
            } catch (NoSuchMethodException e) {
                return "SystemProperties.get(String key, String def) method is not found";
            }
        } catch (ClassNotFoundException e) {
            return "SystemProperties class is not found";
        }
    }

    /**
     * 获取设备唯一标识 本方法调用需要READ_PHONE_STATE权限
     *
     * @param context
     * @return
     */
    public static String getUUID(Context context) {
        String tmDevice = "", tmSerial = "", tmPhone = "", androidId = "";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                tmDevice = "" + tm.getDeviceId();
                tmSerial = "" + tm.getSimSerialNumber();
                androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            } catch (Exception e) {
                Log.e("AppUtils", "exception:" + e.getMessage());
            }
        } else {
            Log.e("AppUtils", "没有 android.permission.READ_PHONE_STATE 权限");
            tmDevice = "device";
            tmSerial = "serial";
            androidId = "androidid";
        }
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    /*---------------------------------- 命令操作 --------------------------------------*/

    public static void shutdown() {
        ShellUtils.execCommand("reboot -p", true);
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        TyTool.getInstance().getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void reboot() {
        ShellUtils.execCommand("reboot", true);
        Intent intent = new Intent(Intent.ACTION_REBOOT);
        intent.putExtra("nowait", 1);
        intent.putExtra("interval", 1);
        intent.putExtra("window", 0);
        TyTool.getInstance().getContext().sendBroadcast(intent);
    }

    @SuppressLint("MissingPermission")
    public static void reboot(final String reason) {
        PowerManager pm = (PowerManager) TyTool.getInstance().getContext().getSystemService(Context.POWER_SERVICE);
        // noinspection ConstantConditions
        pm.reboot(reason);
    }

    public static void reboot2Recovery() {
        ShellUtils.execCommand("reboot recovery", true);
    }

    public static void reboot2Bootloader() {
        ShellUtils.execCommand("reboot bootloader", true);
    }

    /*---------------------------------- inner methods --------------------------------------*/

    private static boolean isAddressNotInExcepts(final String address, final String... excepts) {
        if (excepts == null || excepts.length == 0) {
            return !"02:00:00:00:00:00".equals(address);
        }
        for (String filter : excepts) {
            if (address.equals(filter)) {
                return false;
            }
        }
        return true;
    }

    @SuppressLint({"HardwareIds", "MissingPermission", "WifiManagerLeak"})
    private static String getMacAddressByWifiInfo() {
        try {
            WifiManager wifi = (WifiManager) TyTool.getInstance().getContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null) {
                    return info.getMacAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static String getMacAddressByNetworkInterface() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (ni == null || !ni.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }
                byte[] macBytes = ni.getHardwareAddress();
                if (macBytes != null && macBytes.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : macBytes) {
                        sb.append(String.format("%02x:", b));
                    }
                    return sb.substring(0, sb.length() - 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static String getMacAddressByInetAddress() {
        try {
            InetAddress inetAddress = getInetAddress();
            if (inetAddress != null) {
                NetworkInterface ni = NetworkInterface.getByInetAddress(inetAddress);
                if (ni != null) {
                    byte[] macBytes = ni.getHardwareAddress();
                    if (macBytes != null && macBytes.length > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (byte b : macBytes) {
                            sb.append(String.format("%02x:", b));
                        }
                        return sb.substring(0, sb.length() - 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static InetAddress getInetAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String hostAddress = inetAddress.getHostAddress();
                        if (hostAddress.indexOf(':') < 0) {
                            return inetAddress;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getMacAddressByFile() {
        ShellUtils.CommandResult result = ShellUtils.execCommand("getprop wifi.interface", false);
        if (result.result == 0) {
            String name = result.successMsg;
            if (name != null) {
                result = ShellUtils.execCommand("cat /sys/class/net/" + name + "/address", false);
                if (result.result == 0) {
                    String address = result.successMsg;
                    if (address != null && address.length() > 0) {
                        return address;
                    }
                }
            }
        }
        return "02:00:00:00:00:00";
    }

    private static boolean isTabletInner(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Nullable
    private static String getLowerCaseName(Properties p, Method get, String key) {
        String name = p.getProperty(key);
        if (name == null) {
            try {
                name = (String) get.invoke(null, key);
            } catch (Exception ignored) {
            }
        }
        if (name != null) {
            name = name.toLowerCase();
        }
        return name;
    }

    /**
     * 获取用户硬件信息
     */
    public static String getMobileInfo() {
        StringBuffer sb = new StringBuffer();
        //通过反射获取用户硬件信息
        try {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                // 暴力反射,获取私有信息
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name + "=" + value);
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取CPU硬件信息
     *
     * @return
     */
    static public String getCpuString() {
        if (Build.CPU_ABI.equalsIgnoreCase("x86")) {
            return "Intel";
        }
        String strInfo = "";
        try {
            byte[] bs = new byte[1024];
            RandomAccessFile reader = new RandomAccessFile("/proc/cpuinfo", "r");
            reader.read(bs);
            String ret = new String(bs);
            int index = ret.indexOf(0);
            if (index != -1) {
                strInfo = ret.substring(0, index);
            } else {
                strInfo = ret;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return strInfo;
    }

    /**
     * 获取CPU硬件类型
     *
     * @return
     */
    static public String getCpuType() {
        String strInfo = getCpuString();
        String strType = null;
        if (strInfo.contains("ARMv5")) {
            strType = "armv5";
        } else if (strInfo.contains("ARMv6")) {
            strType = "armv6";
        } else if (strInfo.contains("ARMv7")) {
            strType = "armv7";
        } else if (strInfo.contains("Intel")) {
            strType = "x86";
        } else {
            strType = "unknown";
            return strType;
        }
        if (strInfo.contains("neon")) {
            strType += "_neon";
        } else if (strInfo.contains("vfpv3")) {
            strType += "_vfpv3";
        } else if (strInfo.contains(" vfp")) {
            strType += "_vfp";
        } else {
            strType += "_none";
        }
        return strType;
    }

    /**
     * 获取CPU参数对象
     *
     * @return
     */
    public static CPUInfo getCPUInfo() {
        String strInfo = null;
        try {
            byte[] bs = new byte[1024];
            RandomAccessFile reader = new RandomAccessFile("/proc/cpuinfo", "r");
            reader.read(bs);
            String ret = new String(bs);
            int index = ret.indexOf(0);
            if (index != -1) {
                strInfo = ret.substring(0, index);
            } else {
                strInfo = ret;
            }
        } catch (IOException ex) {
            strInfo = "";
            ex.printStackTrace();
        }
        CPUInfo info = parseCPUInfo(strInfo);
        info.mCPUMaxFreq = getMaxCpuFreq(0);
        return info;
    }

    private final static String kCpuInfoPath = "/sys/devices/system/cpu/cpu";
    private final static String kMaxFreqFilePath = "/cpufreq/cpuinfo_max_freq";

    /**
     * 获取CPU最大频率
     * @param cpuIndex  CPU核心序号
     * @return
     */
    public static int getMaxCpuFreq(int cpuIndex) {
        int result = -1;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(kCpuInfoPath + cpuIndex + kMaxFreqFilePath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            if (text != null) {
                result = Integer.parseInt(text.trim());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private final static String kMinFreqFilePath = "/cpufreq/cpuinfo_min_freq";

    /**
     * 获取CPU最小频率
     * @param cpuIndex  CPU核心序号
     * @return
     */
    public static int getMinCpuFreq(int cpuIndex) {
        int result = -1;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(kCpuInfoPath + cpuIndex + kMinFreqFilePath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            if (text != null) {
                result = Integer.parseInt(text.trim());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private final static String kCurFreqFilePath = "/cpufreq/cpuinfo_cur_freq";

    /**
     * 获取CPU当前频率
     * @param cpuIndex  CPU核心序号
     * @return
     */
    public static int getCurCpuFreq(int cpuIndex) {
        int result = -1;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(kCpuInfoPath + cpuIndex + kCurFreqFilePath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            if (text != null) {
                result = Integer.parseInt(text.trim());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static class CPUInfo {
        public CPUInfo() {
        }

        public static final int CPU_TYPE_UNKNOWN = 0x00000000;
        public static final int CPU_TYPE_ARMV5TE = 0x00000001;
        public static final int CPU_TYPE_ARMV6 = 0x00000010;
        public static final int CPU_TYPE_ARMV7 = 0x00000100;
        public static final int CPU_FEATURE_UNKNOWS = 0x00000000;
        public static final int CPU_FEATURE_VFP = 0x00000001;
        public static final int CPU_FEATURE_VFPV3 = 0x00000010;
        public static final int CPU_FEATURE_NEON = 0x00000100;
        public int mCPUType;
        public int mCPUCount;
        public int mCPUFeature;
        public double mBogoMips;
        public long mCPUMaxFreq;

        @Override
        public String toString() {
            return "CPUInfo{" +
                    "mCPUType=" + mCPUType +
                    ", mCPUCount=" + mCPUCount +
                    ", mCPUFeature=" + mCPUFeature +
                    ", mBogoMips=" + mBogoMips +
                    ", mCPUMaxFreq=" + mCPUMaxFreq +
                    '}';
        }
    }

    /**
     * 解析CPU信息
     *
     * @param cpuInfo
     * @return
     * @hide
     */
    private static CPUInfo parseCPUInfo(String cpuInfo) {
        if (cpuInfo == null || "".equals(cpuInfo)) {
            return null;
        }
        CPUInfo ci = new CPUInfo();
        ci.mCPUType = CPUInfo.CPU_TYPE_UNKNOWN;
        ci.mCPUFeature = CPUInfo.CPU_FEATURE_UNKNOWS;
        ci.mCPUCount = 1;
        ci.mBogoMips = 0;
        if (cpuInfo.contains("ARMv5")) {
            ci.mCPUType = CPUInfo.CPU_TYPE_ARMV5TE;
        } else if (cpuInfo.contains("ARMv6")) {
            ci.mCPUType = CPUInfo.CPU_TYPE_ARMV6;
        } else if (cpuInfo.contains("ARMv7")) {
            ci.mCPUType = CPUInfo.CPU_TYPE_ARMV7;
        }
        if (cpuInfo.contains("neon")) {
            ci.mCPUFeature |= CPUInfo.CPU_FEATURE_NEON;
        }
        if (cpuInfo.contains("vfpv3")) {
            ci.mCPUFeature |= CPUInfo.CPU_FEATURE_VFPV3;
        }
        if (cpuInfo.contains(" vfp")) {
            ci.mCPUFeature |= CPUInfo.CPU_FEATURE_VFP;
        }
        String[] items = cpuInfo.split("\n");
        for (String item : items) {
            if (item.contains("CPU variant")) {
                int index = item.indexOf(": ");
                if (index >= 0) {
                    String value = item.substring(index + 2);
                    try {
                        ci.mCPUCount = Integer.decode(value);
                        ci.mCPUCount = ci.mCPUCount == 0 ? 1 : ci.mCPUCount;
                    } catch (NumberFormatException e) {
                        ci.mCPUCount = 1;
                    }
                }
            } else if (item.contains("BogoMIPS")) {
                int index = item.indexOf(": ");
                if (index >= 0) {
                    String value = item.substring(index + 2);
                }
            }
        }
        return ci;
    }

    /**
     * 获取设备内存大小值
     *
     * @return 内存大小, 单位MB
     */
    public static long getMemoryTotalSize() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            if (str2 != null) {
                arrayOfString = str2.split("\\s+");
                initial_memory = Integer.valueOf(arrayOfString[1]).intValue() / 1024;
            }
            localBufferedReader.close();
            return initial_memory;
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * 获得可用的内存
     * @param mContext
     * @return
     */
    public static double getMemoryAvailableSize(Context mContext) {
        double mem_available_size;
        // 得到ActivityManager
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 创建ActivityManager.MemoryInfo对象
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // 取得剩余的内存空间
        mem_available_size = mi.availMem / 1024.0 / 1024;
        return mem_available_size;
    }

    /**
     * 获取android CPU类型
     *
     * @return String CPU类型
     */
    public static String getCpuModel() {
        String cpu_model = "";
        CPUInfo in = getCPUInfo();
        if ((in.mCPUType & CPUInfo.CPU_TYPE_ARMV5TE) == CPUInfo.CPU_TYPE_ARMV5TE) {
            cpu_model = "armv5";
        } else if ((in.mCPUType & CPUInfo.CPU_TYPE_ARMV6) == CPUInfo.CPU_TYPE_ARMV6) {
            cpu_model = "armv6";
        } else if ((in.mCPUType & CPUInfo.CPU_TYPE_ARMV7) == CPUInfo.CPU_TYPE_ARMV7) {
            cpu_model = "armv7";
        } else {
            cpu_model = "unknown";
        }
        return cpu_model;
    }

    /**
     * 获取android CPU特性
     *
     * @return String CPU特性
     */
    public static String getCpuFeature() {
        String cpu_feature = "";
        CPUInfo in = getCPUInfo();
        if ((in.mCPUFeature & CPUInfo.CPU_FEATURE_NEON) == CPUInfo.CPU_FEATURE_NEON) {
            cpu_feature = "neon";
        } else if ((in.mCPUFeature & CPUInfo.CPU_FEATURE_VFP) == CPUInfo.CPU_FEATURE_VFP) {
            cpu_feature = "vfp";
        } else if ((in.mCPUFeature & CPUInfo.CPU_FEATURE_VFPV3) == CPUInfo.CPU_FEATURE_VFPV3) {
            cpu_feature = "vfpv3";
        } else {
            cpu_feature = "unknown";
        }
        return cpu_feature;
    }

    /**
     * 获取CPU使用情况(top -n 1 -m 5 命令)
     */
    public static String getCpuRate_top() {
        //CPU 使用率
        String result;
        Process p = null;
        StringBuilder sb = new StringBuilder();
        try {
            p = Runtime.getRuntime().exec("top -n 1 -m 5");
            BufferedReader br = new BufferedReader(new InputStreamReader
                    (p.getInputStream()));
            while ((result = br.readLine()) != null) {
                if (result.trim().length() < 1) {
                    continue;
                } else {
                    sb.append(result + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取CPU温度
     * @return
     */
    public static String getCpuTemp() {
        String temp = "Unknow";
        BufferedReader br = null;
        FileReader fr = null;
        try {
            File dir = new File("/sys/class/thermal/");
            File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (Pattern.matches("thermal_zone[0-9]+", file.getName())) {
                        return true;
                    }
                    return false;
                }
            });
            final int SIZE = files.length;
            String line = "";
            String type = "";
            for (int i = 0; i < SIZE; i++) {
                fr = new FileReader("/sys/class/thermal/thermal_zone" + i + "/type");
                br = new BufferedReader(fr);
                line = br.readLine();
                if (line != null) {
                    type = line;
                }
                fr = new FileReader("/sys/class/thermal/thermal_zone" + i + "/temp");
                br = new BufferedReader(fr);
                line = br.readLine();
                if (line != null) {
                    // MTK CPU
                    if (type.contains("cpu")) {
                        long temperature = Long.parseLong(line);
                        if (temperature < 0) {
                            temp = "Unknow";
                        } else {
                            temp = (float) (temperature / 1000.0) + "";
                        }
                    } else if (type.contains("tsens_tz_sensor")) {
                        // Qualcomm CPU
                        long temperature = Long.parseLong(line);
                        if (temperature < 0) {
                            temp = "Unknow";
                        } else if (temperature > 100) {
                            temp = (float) (temperature / 10.0) + "";
                        } else {
                            temp = temperature + "";
                        }
                    }

                }
            }
            if (fr != null) {
                fr.close();
            }
            if (br != null) {
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return temp;
    }

    /**
     * 获取CPU温度
     * @return
     */
    public static long getCpuTemp2() {
        //CPU温度
        int result = -1;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader("/sys/class/thermal/thermal_zone8/temp");
            br = new BufferedReader(fr);
            String text = br.readLine();
            if (text != null) {
                result = Integer.parseInt(text.trim());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 获取SD卡的已使用百分百
     *
     * @return
     */
    public static double getSDCardUsedRate() {
        File esd = Environment.getExternalStorageDirectory();
        if (isSDCardEnable() && esd != null) {
            return (esd.getTotalSpace() - esd.getFreeSpace()) * 1000.0 / esd.getTotalSpace();
        }
        return -1;
    }

    /**
     * 获取SD卡的总容量 单位:M
     *
     * @return
     */
    public static double getSDCardAllSize() {
        File esd = Environment.getExternalStorageDirectory();
        if (isSDCardEnable() && esd != null) {
            return esd.getTotalSpace() / 1024.0 / 1024;
        }
        return -1;
    }

    /**
     * 获取SD卡的剩余容量 单位:M
     *
     * @return
     */
    public static double getSDCardAvailableSize() {
        File esd = Environment.getExternalStorageDirectory();
        if (isSDCardEnable() && esd != null) {
            return esd.getFreeSpace() / 1024.0 / 1024;
        }
        return -1;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 获取系统存储路径
     *
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }
}
