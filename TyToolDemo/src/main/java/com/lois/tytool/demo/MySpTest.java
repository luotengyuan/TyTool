package com.lois.tytool.demo;

import com.lois.tytool.sharedpreferences.annotation.BooleanEntity;
import com.lois.tytool.sharedpreferences.annotation.FloatEntity;
import com.lois.tytool.sharedpreferences.annotation.IntEntity;
import com.lois.tytool.sharedpreferences.annotation.LongEntity;
import com.lois.tytool.sharedpreferences.annotation.PreferencesClass;
import com.lois.tytool.sharedpreferences.annotation.StringEntity;

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
