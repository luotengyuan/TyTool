package com.lois.tytool.sharedpreferences.compiler.method;

import com.squareup.javapoet.MethodSpec;

/**
 * @Description 方法生成基础类
 * @Author Luo.T.Y
 * @Date 2022/2/21
 * @Time 20:51
 */
public abstract class BaseMethod {
    protected String key;
    protected String baseMethodName;
    protected String instanceName;

    public abstract MethodSpec render();

    public BaseMethod(String key, String instanceName) {
        this.key = key;
        this.instanceName = instanceName;
        this.baseMethodName = formatKey(key);
    }

    private String formatKey(String key) {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException("key is null");
        }
        StringBuilder sb = new StringBuilder();
        if (key.contains("_")) {
            String[] separatedKeys = key.split("_");
            for (String separatedKey : separatedKeys) {
                sb.append(separatedKey.substring(0, 1).toUpperCase() + separatedKey.substring(1));
            }
        } else {
            sb.append(key.substring(0, 1).toUpperCase() + key.substring(1));
        }
        return sb.toString();
    }
}
