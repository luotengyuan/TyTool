package com.lois.tytool;

import android.util.Log;

import com.lois.tytool.base.constant.FileConstants;
import com.lois.tytool.base.io.LogSaveUtils;
import com.lois.tytool.base.time.DateTimeUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @Description 控制台打印各类信息,可自定义添加相关打印类
 * @Author Luo.T.Y
 * @Date 2017-09-22
 * @Time 11:32
 */
public class TyLog extends com.lois.tytool.base.debug.TyLog {

    public static void v(String msg) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            v(tag, msg);
        }
    }

    public static void v(Object obj) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            String msg = getObjString(obj);
            v(tag, msg);
        }
    }

    public static <E> void v(Collection<E> collection) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH && collection != null) {
            String tag = getSimpleClassName();
            for (Object obj: collection) {
                v(tag, getObjString(obj));
            }
        }
    }

    public static <A> void v(A[] arrays) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH && arrays != null) {
            String tag = getSimpleClassName();
            for (Object obj : arrays) {
                v(tag, getObjString(obj));
            }
        }
    }

    public static <K,V> void v(Map<K, V> maps) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH && maps != null) {
            String tag = getSimpleClassName();
            for (Map.Entry<K, V> entry : maps.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                String ks = getObjString(k);
                String vs = getObjString(v);
                v(tag, ks + " --> " + vs);
            }
        }
    }

    public static void v(String tag, Object obj) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg = getObjString(obj);
            v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Object obj) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg_obj = getObjString(obj);
            v(tag, msg + "  " + msg_obj);
        }
    }

    public static <E> void v(String tag, Collection<E> collection) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH && collection != null) {
            for (Object obj: collection) {
                v(tag, getObjString(obj));
            }
        }
    }

    public static <A> void v(String tag, A[] arrays) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH && arrays != null) {
            for (Object obj : arrays) {
                v(tag, getObjString(obj));
            }
        }
    }

    public static <K,V> void v(String tag, Map<K, V> maps) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH && maps != null) {
            for (Map.Entry<K, V> entry : maps.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                String ks = getObjString(k);
                String vs = getObjString(v);
                v(tag, ks + " --> " + vs);
            }
        }
    }

    public static void v(String tag, String msg, boolean showMethodAndLine) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            if (showMethodAndLine) {
                v(tag, msg + FileConstants.NEWLINE_STR_LINUX + callMethodAndLine());
            } else {
                v(tag, msg);
            }
        }
    }

    public static void v(String tag, String msg, boolean showMethodAndLine, boolean showCallHierarchy) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            if (showCallHierarchy) {
                v(tag, msg + FileConstants.NEWLINE_STR_LINUX + getCallHierarchy());
            } else if (showMethodAndLine) {
                v(tag, msg + FileConstants.NEWLINE_STR_LINUX + callMethodAndLine());
            } else {
                v(tag, msg);
            }
        }
    }

    public static void v(String tag, String msg) {
        if (VERBOSE >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            Log.v(tag, msg == null ? "null" : msg);
            if (VERBOSE >= MIN_SAVE_LEVEL) {
                // 保存日志到文件
                LogSaveUtils.saveLog(LOG_SAVE_PATH + "logcat_" + DateTimeUtils.getDate() + ".txt", String.format("%s  %s/%s  %s\r\n", DateTimeUtils.getDateTime(), "V", tag, msg));
            }
        }
    }

    public static void vFormat(String format, Object... args) {
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
            v(tag, msg);
        }
    }

    public static void vFormat(String tag, String format, Object... args) {
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
            v(tag, msg);
        }
    }

    public static void d(String msg) {
        if (DEBUG >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            d(tag, msg);
        }
    }

    public static void d(Object obj) {
        if (DEBUG >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            String msg = getObjString(obj);
            d(tag, msg);
        }
    }

    public static <E> void d(Collection<E> collection) {
        if (DEBUG >= MIN_SHOW_LEVEL && LOG_SWITCH && collection != null) {
            String tag = getSimpleClassName();
            for (Object obj: collection) {
                d(tag, getObjString(obj));
            }
        }
    }

    public static <A> void d(A[] arrays) {
        if (DEBUG >= MIN_SHOW_LEVEL && LOG_SWITCH && arrays != null) {
            String tag = getSimpleClassName();
            for (Object obj : arrays) {
                d(tag, getObjString(obj));
            }
        }
    }

    public static <K,V> void d(Map<K, V> maps) {
        if (DEBUG >= MIN_SHOW_LEVEL && LOG_SWITCH && maps != null) {
            String tag = getSimpleClassName();
            for (Map.Entry<K, V> entry : maps.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                String ks = getObjString(k);
                String vs = getObjString(v);
                d(tag, ks + " --> " + vs);
            }
        }
    }

    public static void d(String tag, Object obj) {
        if (DEBUG >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg = getObjString(obj);
            d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Object obj) {
        if (DEBUG >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg_obj = getObjString(obj);
            d(tag, msg + "  " + msg_obj);
        }
    }

    public static <E> void d(String tag, Collection<E> collection) {
        if (DEBUG >= MIN_SHOW_LEVEL && LOG_SWITCH && collection != null) {
            for (Object obj: collection) {
                d(tag, getObjString(obj));
            }
        }
    }

    public static <A> void d(String tag, A[] arrays) {
        if (DEBUG >= MIN_SHOW_LEVEL && LOG_SWITCH && arrays != null) {
            for (Object obj : arrays) {
                d(tag, getObjString(obj));
            }
        }
    }

    public static <K,V> void d(String tag, Map<K, V> maps) {
        if (DEBUG >= MIN_SHOW_LEVEL && LOG_SWITCH && maps != null) {
            for (Map.Entry<K, V> entry : maps.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                String ks = getObjString(k);
                String vs = getObjString(v);
                d(tag, ks + " --> " + vs);
            }
        }
    }

    public static void d(String tag, String msg, boolean showMethodAndLine) {
        if (DEBUG >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            if (showMethodAndLine) {
                d(tag, msg + FileConstants.NEWLINE_STR_LINUX + callMethodAndLine());
            } else {
                d(tag, msg);
            }
        }
    }

    public static void d(String tag, String msg, boolean showMethodAndLine, boolean showCallHierarchy) {
        if (DEBUG >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            if (showCallHierarchy) {
                d(tag, msg + FileConstants.NEWLINE_STR_LINUX + getCallHierarchy());
            } else if (showMethodAndLine) {
                d(tag, msg + FileConstants.NEWLINE_STR_LINUX + callMethodAndLine());
            } else {
                d(tag, msg);
            }
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            Log.d(tag, msg == null ? "null" : msg);
            if (DEBUG >= MIN_SAVE_LEVEL) {
                // 保存日志到文件
                LogSaveUtils.saveLog(LOG_SAVE_PATH + "logcat_" + DateTimeUtils.getDate() + ".txt", String.format("%s  %s/%s  %s\r\n", DateTimeUtils.getDateTime(), "D", tag, msg));
            }
        }
    }

    public static void dFormat(String format, Object... args) {
        if (DEBUG >= MIN_SHOW_LEVEL && LOG_SWITCH) {
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
            d(tag, msg);
        }
    }

    public static void dFormat(String tag, String format, Object... args) {
        if (DEBUG >= MIN_SHOW_LEVEL && LOG_SWITCH) {
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
            d(tag, msg);
        }
    }

    public static void i(String msg) {
        if (INFO >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            i(tag, msg);
        }
    }

    public static void i(Object obj) {
        if (INFO >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            String msg = getObjString(obj);
            i(tag, msg);
        }
    }

    public static <E> void i(Collection<E> collection) {
        if (INFO >= MIN_SHOW_LEVEL && LOG_SWITCH && collection != null) {
            String tag = getSimpleClassName();
            for (Object obj: collection) {
                i(tag, getObjString(obj));
            }
        }
    }

    public static <A> void i(A[] arrays) {
        if (INFO >= MIN_SHOW_LEVEL && LOG_SWITCH && arrays != null) {
            String tag = getSimpleClassName();
            for (Object obj : arrays) {
                i(tag, getObjString(obj));
            }
        }
    }

    public static <K,V> void i(Map<K, V> maps) {
        if (INFO >= MIN_SHOW_LEVEL && LOG_SWITCH && maps != null) {
            String tag = getSimpleClassName();
            for (Map.Entry<K, V> entry : maps.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                String ks = getObjString(k);
                String vs = getObjString(v);
                i(tag, ks + " --> " + vs);
            }
        }
    }

    public static void i(String tag, Object obj) {
        if (INFO >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg = getObjString(obj);
            i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Object obj) {
        if (INFO >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg_obj = getObjString(obj);
            i(tag, msg + "  " + msg_obj);
        }
    }

    public static <E> void i(String tag, Collection<E> collection) {
        if (INFO >= MIN_SHOW_LEVEL && LOG_SWITCH && collection != null) {
            for (Object obj: collection) {
                i(tag, getObjString(obj));
            }
        }
    }

    public static <A> void i(String tag, A[] arrays) {
        if (INFO >= MIN_SHOW_LEVEL && LOG_SWITCH && arrays != null) {
            for (Object obj : arrays) {
                i(tag, getObjString(obj));
            }
        }
    }

    public static <K,V> void i(String tag, Map<K, V> maps) {
        if (INFO >= MIN_SHOW_LEVEL && LOG_SWITCH && maps != null) {
            for (Map.Entry<K, V> entry : maps.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                String ks = getObjString(k);
                String vs = getObjString(v);
                i(tag, ks + " --> " + vs);
            }
        }
    }

    public static void i(String tag, String msg, boolean showMethodAndLine) {
        if (INFO >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            if (showMethodAndLine) {
                i(tag, msg + FileConstants.NEWLINE_STR_LINUX + callMethodAndLine());
            } else {
                i(tag, msg);
            }
        }
    }

    public static void i(String tag, String msg, boolean showMethodAndLine, boolean showCallHierarchy) {
        if (INFO >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            if (showCallHierarchy) {
                i(tag, msg + FileConstants.NEWLINE_STR_LINUX + getCallHierarchy());
            } else if (showMethodAndLine) {
                i(tag, msg + FileConstants.NEWLINE_STR_LINUX + callMethodAndLine());
            } else {
                i(tag, msg);
            }
        }
    }

    public static void i(String tag, String msg) {
        if (INFO >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            Log.i(tag, msg == null ? "null" : msg);
            if (INFO >= MIN_SAVE_LEVEL) {
                // 保存日志到文件
                LogSaveUtils.saveLog(LOG_SAVE_PATH + "logcat_" + DateTimeUtils.getDate() + ".txt", String.format("%s  %s/%s  %s\r\n", DateTimeUtils.getDateTime(), "I", tag, msg));
            }
        }
    }

    public static void iFormat(String format, Object... args) {
        if (INFO >= MIN_SHOW_LEVEL && LOG_SWITCH) {
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
            i(tag, msg);
        }
    }

    public static void iFormat(String tag, String format, Object... args) {
        if (INFO >= MIN_SHOW_LEVEL && LOG_SWITCH) {
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
            i(tag, msg);
        }
    }

    public static void w(String msg) {
        if (WARN >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            w(tag, msg);
        }
    }

    public static void w(Object obj) {
        if (WARN >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            String msg = getObjString(obj);
            w(tag, msg);
        }
    }

    public static <E> void w(Collection<E> collection) {
        if (WARN >= MIN_SHOW_LEVEL && LOG_SWITCH && collection != null) {
            String tag = getSimpleClassName();
            for (Object obj: collection) {
                w(tag, getObjString(obj));
            }
        }
    }

    public static <A> void w(A[] arrays) {
        if (WARN >= MIN_SHOW_LEVEL && LOG_SWITCH && arrays != null) {
            String tag = getSimpleClassName();
            for (Object obj : arrays) {
                w(tag, getObjString(obj));
            }
        }
    }

    public static <K,V> void w(Map<K, V> maps) {
        if (WARN >= MIN_SHOW_LEVEL && LOG_SWITCH && maps != null) {
            String tag = getSimpleClassName();
            for (Map.Entry<K, V> entry : maps.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                String ks = getObjString(k);
                String vs = getObjString(v);
                w(tag, ks + " --> " + vs);
            }
        }
    }

    public static void w(String tag, Object obj) {
        if (WARN >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg = getObjString(obj);
            w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Object obj) {
        if (WARN >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg_obj = getObjString(obj);
            w(tag, msg + "  " + msg_obj);
        }
    }

    public static <E> void w(String tag, Collection<E> collection) {
        if (WARN >= MIN_SHOW_LEVEL && LOG_SWITCH && collection != null) {
            for (Object obj: collection) {
                w(tag, getObjString(obj));
            }
        }
    }

    public static <A> void w(String tag, A[] arrays) {
        if (WARN >= MIN_SHOW_LEVEL && LOG_SWITCH && arrays != null) {
            for (Object obj : arrays) {
                w(tag, getObjString(obj));
            }
        }
    }

    public static <K,V> void w(String tag, Map<K, V> maps) {
        if (WARN >= MIN_SHOW_LEVEL && LOG_SWITCH && maps != null) {
            for (Map.Entry<K, V> entry : maps.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                String ks = getObjString(k);
                String vs = getObjString(v);
                w(tag, ks + " --> " + vs);
            }
        }
    }

    public static void w(String tag, String msg, boolean showMethodAndLine) {
        if (WARN >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            if (showMethodAndLine) {
                w(tag, msg + FileConstants.NEWLINE_STR_LINUX + callMethodAndLine());
            } else {
                w(tag, msg);
            }
        }
    }

    public static void w(String tag, String msg, boolean showMethodAndLine, boolean showCallHierarchy) {
        if (WARN >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            if (showCallHierarchy) {
                w(tag, msg + FileConstants.NEWLINE_STR_LINUX + getCallHierarchy());
            } else if (showMethodAndLine) {
                w(tag, msg + FileConstants.NEWLINE_STR_LINUX + callMethodAndLine());
            } else {
                w(tag, msg);
            }
        }
    }

    public static void w(String tag, String msg) {
        if (WARN >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            Log.w(tag, msg == null ? "null" : msg);
            if (WARN >= MIN_SAVE_LEVEL) {
                // 保存日志到文件
                LogSaveUtils.saveLog(LOG_SAVE_PATH + "logcat_" + DateTimeUtils.getDate() + ".txt", String.format("%s  %s/%s  %s\r\n", DateTimeUtils.getDateTime(), "W", tag, msg));
            }
        }
    }

    public static void wFormat(String format, Object... args) {
        if (WARN >= MIN_SHOW_LEVEL && LOG_SWITCH) {
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
            w(tag, msg);
        }
    }

    public static void wFormat(String tag, String format, Object... args) {
        if (WARN >= MIN_SHOW_LEVEL && LOG_SWITCH) {
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
            w(tag, msg);
        }
    }

    public static void e(String msg) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            e(tag, msg);
        }
    }

    public static void e(Object obj) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String tag = getSimpleClassName();
            String msg = getObjString(obj);
            e(tag, msg);
        }
    }

    public static <E> void e(Collection<E> collection) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH && collection != null) {
            String tag = getSimpleClassName();
            for (Object obj: collection) {
                e(tag, getObjString(obj));
            }
        }
    }

    public static <A> void e(A[] arrays) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH && arrays != null) {
            String tag = getSimpleClassName();
            for (Object obj : arrays) {
                e(tag, getObjString(obj));
            }
        }
    }

    public static <K,V> void e(Map<K, V> maps) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH && maps != null) {
            String tag = getSimpleClassName();
            for (Map.Entry<K, V> entry : maps.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                String ks = getObjString(k);
                String vs = getObjString(v);
                e(tag, ks + " --> " + vs);
            }
        }
    }

    public static void e(String tag, Object obj) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg = getObjString(obj);
            e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Object obj) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            String msg_obj = getObjString(obj);
            e(tag, msg + "  " + msg_obj);
        }
    }

    public static <E> void e(String tag, Collection<E> collection) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH && collection != null) {
            for (Object obj: collection) {
                e(tag, getObjString(obj));
            }
        }
    }

    public static <A> void e(String tag, A[] arrays) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH && arrays != null) {
            for (Object obj : arrays) {
                e(tag, getObjString(obj));
            }
        }
    }

    public static <K,V> void e(String tag, Map<K, V> maps) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH && maps != null) {
            for (Map.Entry<K, V> entry : maps.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                String ks = getObjString(k);
                String vs = getObjString(v);
                e(tag, ks + " --> " + vs);
            }
        }
    }

    public static void e(String tag, String msg, boolean showMethodAndLine) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            if (showMethodAndLine) {
                e(tag, msg + FileConstants.NEWLINE_STR_LINUX + callMethodAndLine());
            } else {
                e(tag, msg);
            }
        }
    }

    public static void e(String tag, String msg, boolean showMethodAndLine, boolean showCallHierarchy) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            if (showCallHierarchy) {
                e(tag, msg + FileConstants.NEWLINE_STR_LINUX + getCallHierarchy());
            } else if (showMethodAndLine) {
                e(tag, msg + FileConstants.NEWLINE_STR_LINUX + callMethodAndLine());
            } else {
                e(tag, msg);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (ERROR >= MIN_SHOW_LEVEL && LOG_SWITCH) {
            Log.e(tag, msg == null ? "null" : msg);
            if (ERROR >= MIN_SAVE_LEVEL) {
                // 保存日志到文件
                LogSaveUtils.saveLog(LOG_SAVE_PATH + "logcat_" + DateTimeUtils.getDate() + ".txt", String.format("%s  %s/%s  %s\r\n", DateTimeUtils.getDateTime(), "E", tag, msg));
            }
        }
    }

    public static void eFormat(String format, Object... args) {
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
            e(tag, msg);
        }
    }

    public static void eFormat(String tag, String format, Object... args) {
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
            e(tag, msg);
        }
    }

}
