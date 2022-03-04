package com.lois.tytool.basej.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * gson
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class JsonUtils {

    private static volatile Gson gson;

    /**
     * 实例化 gson
     * @return
     */
    private static Gson getInstance(){
        if(gson == null){
            synchronized (JsonUtils.class) {
                if(gson == null){
                    gson = new Gson();
                }
            }
        }
        return gson;
    }

    /**
     * java对象序列化为json字符串
     * @param obj
     * @return
     */
    public static String toJson(Object obj){
        return getInstance().toJson(obj);
    }

    /**
     * json字符串反序列化为java对象
     * @param jsonStr
     * @param c
     * @param <T>
     * @return
     */
    public static <T> T toBean(String jsonStr, Class<T> c){
        return getInstance().fromJson(jsonStr, c);
    }

    /**
     * 转成list
     * 解决泛型在编译期类型被擦除导致报错
     *
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(String json, Class<T> cls) {
        Gson gson = new Gson();
        List<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(gson.fromJson(elem, cls));
        }
        return list;
    }


    /**
     * 转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> toListMap(String gsonString) {
        List<Map<String, T>> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString,
                    new TypeToken<List<Map<String, T>>>() {
                    }.getType());
        }
        return list;
    }


    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    public static <T> Map<String, T> toMap(String gsonString) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }

    /**
     * 格式化json字符串
     * @param jsonStr
     * @return
     */
    public static String format(String jsonStr){
        JsonParser parser = new JsonParser();
        JsonElement ele = parser.parse(jsonStr);
        return new GsonBuilder().setPrettyPrinting().create().toJson(ele);
    }

    /**
     * 判断是否是json字符串
     * @param jsonStr
     * @return
     */
    public static boolean isJson(String jsonStr){
        boolean jsonFlag;
        try {
            new JsonParser().parse(jsonStr).getAsJsonObject();
            jsonFlag = true;
        } catch (Exception e) {
            jsonFlag = false;
        }
        return jsonFlag;
    }
}
