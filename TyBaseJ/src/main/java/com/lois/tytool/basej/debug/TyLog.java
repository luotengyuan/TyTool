package com.lois.tytool.basej.debug;

import com.lois.tytool.basej.constant.FileConstants;
import com.lois.tytool.basej.io.FileUtils;
import com.lois.tytool.basej.io.LogSaveUtils;
import com.lois.tytool.basej.time.DateTimeUtils;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * @Description 控制台打印各类信息,可自定义添加相关打印类
 * @Author Luo.T.Y
 * @Date 2022/2/8
 * @Time 20:21
 */
public class TyLog {
    public static final int DEFAULT_ALL = 0;
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;
    public static final int DEFAULT_NONE = Integer.MAX_VALUE;
    /**
     * 日志总开关
     */
    public static boolean LOG_SWITCH = true;
    /**
     * 最小显示的等级
     */
    public static int MIN_SHOW_LEVEL = DEFAULT_ALL;
    /**
     * 最小存储的等级
     */
    public static int MIN_SAVE_LEVEL = DEFAULT_NONE;

    public static String LOG_SAVE_PATH = null;

    public static boolean setLogSaveInfo(String appPath, int minLevel){
        File dir = null;
        if (minLevel != DEFAULT_NONE) {
            // 判断存放日志文件夹是否合法
            if (appPath == null) {
                return false;
            }
            dir = FileUtils.createNewDirectory(appPath);
            if (dir == null) {
                return false;
            }
        }
        MIN_SAVE_LEVEL = minLevel;
        LOG_SAVE_PATH = dir.getAbsolutePath() + FileConstants.FILE_SEPARATOR + "logcat" + FileConstants.FILE_SEPARATOR;
        return true;
    }

    public static void print(String msg) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            print(tag, msg);
        }
    }

    public static void print(Object obj) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            String msg = getObjString(obj);
            print(tag, msg);
        }
    }

    public static <E> void print(Collection<E> collection) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH && collection != null) {
            String tag = getSimpleClassName();
            for (Object obj: collection) {
                print(tag, getObjString(obj));
            }
        }
    }

    public static <A> void print(A[] arrays) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH && arrays != null) {
            String tag = getSimpleClassName();
            for (Object obj : arrays) {
                print(tag, getObjString(obj));
            }
        }
    }

    public static <K,V> void print(Map<K, V> maps) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH && maps != null) {
            String tag = getSimpleClassName();
            for (Map.Entry<K, V> entry : maps.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                String ks = getObjString(k);
                String vs = getObjString(v);
                print(tag, ks + " --> " + vs);
            }
        }
    }

    public static void print(String tag, Object obj) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg = getObjString(obj);
            print(tag, msg);
        }
    }

    public static void print(String tag, String msg, Object obj) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg_obj = getObjString(obj);
            print(tag, msg + "  " + msg_obj);
        }
    }

    public static <E> void print(String tag, Collection<E> collection) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH && collection != null) {
            for (Object obj: collection) {
                print(tag, getObjString(obj));
            }
        }
    }

    public static <A> void print(String tag, A[] arrays) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH && arrays != null) {
            for (Object obj : arrays) {
                print(tag, getObjString(obj));
            }
        }
    }

    public static <K,V> void print(String tag, Map<K, V> maps) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH && maps != null) {
            for (Map.Entry<K, V> entry : maps.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                String ks = getObjString(k);
                String vs = getObjString(v);
                print(tag, ks + " --> " + vs);
            }
        }
    }

    public static void print(String tag, String msg, boolean showMethodAndLine) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            if (showMethodAndLine) {
                print(tag, msg + FileConstants.NEWLINE_STR_LINUX + callMethodAndLine());
            } else {
                print(tag, msg);
            }
        }
    }

    public static void print(String tag, String msg, boolean showMethodAndLine, boolean showCallHierarchy) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            if (showCallHierarchy) {
                print(tag, msg + FileConstants.NEWLINE_STR_LINUX + getCallHierarchy());
            } else if (showMethodAndLine) {
                print(tag, msg + FileConstants.NEWLINE_STR_LINUX + callMethodAndLine());
            } else {
                print(tag, msg);
            }
        }
    }

    public static void print(String tag, String msg) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String log = String.format("%s  %s/%s  %s\r\n", DateTimeUtils.getDateTime(), "V", tag, msg);
            System.out.println(log);
            if (VERBOSE >= MIN_SAVE_LEVEL) {
                // 保存日志到文件
                LogSaveUtils.saveLog(LOG_SAVE_PATH + "logcat_" + DateTimeUtils.getDate() + ".txt", log);
            }
        }
    }

    public static void printFormat(String format, Object... args) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            String msg = null;
            if (format == null) {
                msg = "null";
            } else {
                try {
                    msg = String.format(format, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (msg == null) {
                    msg = "null";
                }
            }
            print(tag, msg);
        }
    }

    public static void printFormat(String tag, String format, Object... args) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg = null;
            if (format == null) {
                msg = "null";
            } else {
                try {
                    msg = String.format(format, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (msg == null) {
                    msg = "null";
                }
            }
            print(tag, msg);
        }
    }

    public static void printError(String msg) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            printError(tag, msg);
        }
    }

    public static void printError(Object obj) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            String msg = getObjString(obj);
            printError(tag, msg);
        }
    }

    public static <E> void printError(Collection<E> collection) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH && collection != null) {
            String tag = getSimpleClassName();
            for (Object obj: collection) {
                printError(tag, getObjString(obj));
            }
        }
    }

    public static <A> void printError(A[] arrays) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH && arrays != null) {
            String tag = getSimpleClassName();
            for (Object obj : arrays) {
                printError(tag, getObjString(obj));
            }
        }
    }

    public static <K,V> void printError(Map<K, V> maps) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH && maps != null) {
            String tag = getSimpleClassName();
            for (Map.Entry<K, V> entry : maps.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                String ks = getObjString(k);
                String vs = getObjString(v);
                printError(tag, ks + " --> " + vs);
            }
        }
    }

    public static void printError(String tag, Object obj) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg = getObjString(obj);
            printError(tag, msg);
        }
    }

    public static void printError(String tag, String msg, Object obj) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg_obj = getObjString(obj);
            printError(tag, msg + "  " + msg_obj);
        }
    }

    public static <E> void printError(String tag, Collection<E> collection) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH && collection != null) {
            for (Object obj: collection) {
                printError(tag, getObjString(obj));
            }
        }
    }

    public static <A> void printError(String tag, A[] arrays) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH && arrays != null) {
            for (Object obj : arrays) {
                printError(tag, getObjString(obj));
            }
        }
    }

    public static <K,V> void printError(String tag, Map<K, V> maps) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH && maps != null) {
            for (Map.Entry<K, V> entry : maps.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                String ks = getObjString(k);
                String vs = getObjString(v);
                printError(tag, ks + " --> " + vs);
            }
        }
    }

    public static void printError(String tag, String msg, boolean showMethodAndLine) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            if (showMethodAndLine) {
                printError(tag, msg + FileConstants.NEWLINE_STR_LINUX + callMethodAndLine());
            } else {
                printError(tag, msg);
            }
        }
    }

    public static void printError(String tag, String msg, boolean showMethodAndLine, boolean showCallHierarchy) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            if (showCallHierarchy) {
                printError(tag, msg + FileConstants.NEWLINE_STR_LINUX + getCallHierarchy());
            } else if (showMethodAndLine) {
                printError(tag, msg + FileConstants.NEWLINE_STR_LINUX + callMethodAndLine());
            } else {
                printError(tag, msg);
            }
        }
    }

    public static void printError(String tag, String msg) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String log = String.format("%s  %s/%s  %s\r\n", DateTimeUtils.getDateTime(), "E", tag, msg);
            System.err.println(log);
            if (ERROR >= MIN_SAVE_LEVEL) {
                // 保存日志到文件
                LogSaveUtils.saveLog(LOG_SAVE_PATH + "logcat_" + DateTimeUtils.getDate() + ".txt", log);
            }
        }
    }

    public static void printErrorFormat(String format, Object... args) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            String msg = null;
            if (format == null) {
                msg = "null";
            } else {
                try {
                    msg = String.format(format, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (msg == null) {
                    msg = "null";
                }
            }
            printError(tag, msg);
        }
    }

    public static void printErrorFormat(String tag, String format, Object... args) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg = null;
            if (format == null) {
                msg = "null";
            } else {
                try {
                    msg = String.format(format, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (msg == null) {
                    msg = "null";
                }
            }
            printError(tag, msg);
        }
    }

    protected static String getFullClassName() {
        String result = "";
        StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[2];
        result = thisMethodStack.getClassName();
        return result;
    }

    protected static String getSimpleClassName() {
        String result = "";
        StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[2];
        String[] strs = thisMethodStack.getClassName().split("[.]");
        result = strs[strs.length - 1];
        return result;
    }

    protected static String callMethodAndLine() {
        String result = "at ";
        StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[2];
        result += thisMethodStack.toString();
        return result;
    }

    protected static String getCallHierarchy() {
        String result = "at ";
        StackTraceElement[] trace = (new Exception()).getStackTrace();
        for (int i = 2; i < trace.length; i++) {
            if (i == 2) {
                result += trace[i].toString();
            } else {
                result += FileConstants.NEWLINE_STR_LINUX + trace[i].toString();
            }
        }
        return result;
    }

    protected static String getObjString(Object obj){
        if (obj == null) {
            return "null";
        }
        String ret = "";
        if (obj instanceof Object[]) {
            for (Object e : (Object[]) obj) {
                ret += e.toString() + " ";
            }
        } else if (obj instanceof boolean[]) {
            for (boolean e : (boolean[]) obj) {
                ret += e + " ";
            }
        } else if (obj instanceof byte[]) {
            for (byte e : (byte[]) obj) {
                ret += e + " ";
            }
        } else if (obj instanceof char[]) {
            for (char e : (char[]) obj) {
                ret += e + " ";
            }
        } else if (obj instanceof double[]) {
            for (double e : (double[]) obj) {
                ret += e + " ";
            }
        } else if (obj instanceof float[]) {
            for (float e : (float[]) obj) {
                ret += e + " ";
            }
        } else if (obj instanceof int[]) {
            for (int e : (int[]) obj) {
                ret += e + " ";
            }
        } else if (obj instanceof long[]) {
            for (long e : (long[]) obj) {
                ret += e + " ";
            }
        } else if (obj instanceof short[]) {
            for (short e : (short[]) obj) {
                ret += e + " ";
            }
        } else {
            ret = obj.toString();
        }
        return ret;
    }
}
