package com.lois.tytool.activity;

import android.os.Bundle;

/**
 * 实现向右滑动关闭界面的Activity基类<p>
 * 使用说明：<p>
 * (1)将需要滑动关闭界面的Activity继承 SlideActivity 。<p>
 * (2)将需要滑动关闭界面的 Activity的 theme 指定为自定义的 AppTheme.Activity.Slide 。<p>
 * (3)将不需要滑动关闭界面的 Activity（如App主界面S的theme指定为AppTheme，重写enableSliding并返回 false 。<p>
 *
 * 需要将相关activity的顶层布局背景色设为activity_sliding_bg
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public abstract class BaseSlidingAppComatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (enableSliding()) {
            SlidingLayout rootView = new SlidingLayout(this);
            rootView.bindActivity(this);
        }
    }

    /**
     * 是否可以使用向右滑动关闭界面
     * @return true代表可以，false代表不可以
     */
    protected boolean enableSliding() {
        return true;
    }
}
