package com.lois.tytool.activity;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * MVP框架模式的View层基类，用于给Activity继承
 * presenter绑定到activity和View的绑定和解绑操作是每个Activity都会去做的，用父类通过泛型统一完成这个工作。
 * @param <V> View层接口
 * @param <P> Presenter层类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public abstract class BaseMvpActivity<V,P extends BasePresenter<V>> extends BaseSlidingAppComatActivity {

    final static String TAG="BaseMvpActivity";

    protected ProgressDialog mProgressDialog;

    /**
     * Presenter对象
     */
    protected P mPresenter;

    /**
     * 判断视图是否还在活动
     */
    protected boolean isActive;

    /**
     * 实例化Presenter，注意是实现泛型指定的Presenter
     * @return
     */
    protected abstract P initPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = initPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.attachView((V) this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

}
