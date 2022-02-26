package com.lois.tytool.activity;

import android.app.Activity;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Activity animation direction enums.
 *
 * For usages see:
 * 1. {@link ActivityUtils#finishActivity(Activity, int)}
 * 2. {@link ActivityUtils#overridePendingTransition(Activity, int)}
 * etc.
 *
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
@IntDef({ActivityDirection.ANIMATE_NONE,
        ActivityDirection.ANIMATE_FORWARD,
        ActivityDirection.ANIMATE_BACK,
        ActivityDirection.ANIMATE_EASE_IN_OUT,
        ActivityDirection.ANIMATE_SLIDE_TOP_FROM_BOTTOM,
        ActivityDirection.ANIMATE_SLIDE_BOTTOM_FROM_TOP,
        ActivityDirection.ANIMATE_SCALE_IN,
        ActivityDirection.ANIMATE_SCALE_OUT})
@Retention(RetentionPolicy.SOURCE)
public @interface ActivityDirection {
    int ANIMATE_NONE                        = 0x00;
    int ANIMATE_FORWARD                     = 0x01;
    int ANIMATE_BACK                        = 0x02;
    int ANIMATE_EASE_IN_OUT                 = 0x03;
    int ANIMATE_SLIDE_TOP_FROM_BOTTOM       = 0x04;
    int ANIMATE_SLIDE_BOTTOM_FROM_TOP       = 0x05;
    int ANIMATE_SCALE_IN                    = 0x06;
    int ANIMATE_SCALE_OUT                   = 0x07;
}
