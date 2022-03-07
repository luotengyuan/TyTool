package com.lois.tytool.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.lois.tytool.TyTool;

/**
 * @Description SharedPreferences 工具类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 9:43
 */
public class SharedPreferencesUtils {
    /**
     * 存储的SharedPreferences文件名
     */
    private static String FILE_NAME = "my_sp_config";
    /**
     * 存储文件模式
     */
    private static int MODE = Context.MODE_PRIVATE;
    /**
     * SharedPreferences对象
     */
    private static SharedPreferences mPreferences;
    /**
     * SharedPreferences编辑器
     */
    private static SharedPreferences.Editor mEditor;

    /**
     * 使用默认参数初始化
     */
    public static void init() {
        init(FILE_NAME, MODE);
    }

    /**
     * 使用指定的文件名初始化
     * @param fileName 指定文件名
     */
    public static void init(String fileName) {
        init(fileName, MODE);
    }

    /**
     * 使用指定的文件名和模式初始化
     * @param fileName 指定的文件名
     * @param mode 指定的模式
     */
    public static void init(String fileName, int mode) {
        if (fileName != null) {
            FILE_NAME = fileName;
        }
        MODE = mode;
        mPreferences = TyTool.getInstance().getContext().getSharedPreferences(FILE_NAME, MODE);
        mEditor = mPreferences.edit();
    }

    /**
     * 保存数据到文件
     * @param key 关键字
     * @param data 数据
     * @return 是否成功
     */
    public static boolean setData(String key,Object data){
        if (mPreferences == null || mEditor == null) {
            init();
        }
        String type = data.getClass().getSimpleName();
        if ("Integer".equals(type)){
            mEditor.putInt(key, (Integer)data);
        }else if ("Boolean".equals(type)){
            mEditor.putBoolean(key, (Boolean)data);
        }else if ("String".equals(type)){
            mEditor.putString(key, (String)data);
        }else if ("Float".equals(type)){
            mEditor.putFloat(key, (Float)data);
        }else if ("Long".equals(type)){
            mEditor.putLong(key, (Long)data);
        }
        return mEditor.commit();
    }

    /**
     * 从文件中读取数据
     * @param key 关键字
     * @param defValue 默认值
     * @return 返回值
     */
    public static Object getData(String key, Object defValue){
        if (mPreferences == null || mEditor == null) {
            init();
        }
        String type = defValue.getClass().getSimpleName();
        //defValue为为默认值，如果当前获取不到数据就返回它
        if ("Integer".equals(type)){
            return mPreferences.getInt(key, (Integer)defValue);
        }else if ("Boolean".equals(type)){
            return mPreferences.getBoolean(key, (Boolean)defValue);
        }else if ("String".equals(type)){
            return mPreferences.getString(key, (String)defValue);
        }else if ("Float".equals(type)){
            return mPreferences.getFloat(key, (Float)defValue);
        }else if ("Long".equals(type)){
            return mPreferences.getLong(key, (Long)defValue);
        }
        return null;
    }

    /**
     * 从文件中删除指定数据
     * @param key 关键字
     * @return 是否成功
     */
    public static boolean removeData(String key){
        if (mPreferences == null || mEditor == null) {
            init();
        }
        mEditor.remove(key);
        return mEditor.commit();
    }

    /**
     * 从文件中删除所有数据
     * @return 是否成功
     */
    public static boolean removeAllData(){
        if (mPreferences == null || mEditor == null) {
            init();
        }
        mEditor.clear();
        return mEditor.commit();
    }
}
