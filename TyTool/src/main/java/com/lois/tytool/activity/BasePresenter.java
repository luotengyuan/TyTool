package com.lois.tytool.activity;

import android.os.Handler;
import android.os.Looper;

/**
 * Presenter基类
 * <p>
 * 应用程序主要的程序逻辑在Presenter内实现，而且Presenter将Model和View完全分离，所有的交互都发生在Presenter内部。
 * 具体业务逻辑全部由Presenter接口实现类中完成。
 * </p>
 * @param <V> view层接口（activity/fragment）
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public abstract class BasePresenter<V> {

    private final static String TAG="BasePresenter";

    /**
     * View层接口的引用
     */
    protected V mView;

    protected Handler mHandler;

    public BasePresenter(){
        //当子类初始化时，会首先调用父类的构造方法，然后才调用子类的构造方法
        //因此，可以将一些子类用到的成员放到父类当中，由父类的构造方法进行初始化。
        mHandler=new Handler(Looper.getMainLooper());
    }

    /**
     * 绑定View
     * @param view
     */
    public void attachView(V view) {
        this.mView=view;
    }

    /**
     * 解绑View
     */
    public void detachView() {
        mView=null;
    }
}
