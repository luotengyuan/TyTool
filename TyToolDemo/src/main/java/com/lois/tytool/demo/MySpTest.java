package com.lois.tytool.demo;

import com.lois.sp.annotation.BooleanEntity;
import com.lois.sp.annotation.FloatEntity;
import com.lois.sp.annotation.IntEntity;
import com.lois.sp.annotation.LongEntity;
import com.lois.sp.annotation.PreferencesClass;
import com.lois.sp.annotation.StringEntity;

/**
 * description:.
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
@PreferencesClass(prefsFileName = "mysp")
public class MySpTest {
    @StringEntity(key = "name", defaultValue = "hah")
    String NAME;

    @BooleanEntity(key = "is_chinese", defaultValue = false)
    boolean IS_CHINESE;

    @IntEntity(key = "age", defaultValue = 16)
    int KEY_AGE;

    @FloatEntity(key = "hight", defaultValue = 16.6f)
    float KEY_HEIGHT;

    @LongEntity(key = "number", defaultValue = 188L)
    long KEY_NUMBER;
}
