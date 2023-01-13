package com.lois.tytool.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment基类
 * 注意：导入的包是support.v4.app.Fragment;
 *
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public abstract class BaseFragment extends Fragment {
    private final String TAG = BaseFragment.class.getSimpleName();
    private Unbinder unbinder;

    /**
     * 基础视图
     */
    protected View mBaseView;

    /**
     * 上下文
     */
    protected Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        initFragment();
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 初始化Fragment
     */
    protected abstract void initFragment();

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 进入指定的Activity
     *
     * @param cls
     */
    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent(mContext, cls);
        startActivity(intent);
    }

}
