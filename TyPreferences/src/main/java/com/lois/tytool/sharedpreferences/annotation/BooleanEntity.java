package com.lois.tytool.sharedpreferences.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description SharedPreferences Boolean 类型数据注解
 * @Author Luo.T.Y
 * @Date 2022/2/21
 * @Time 20:51
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface BooleanEntity {
    String key();
    boolean defaultValue();
}
