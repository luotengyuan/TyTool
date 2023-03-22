package com.lois.tytool.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lois.tytool.R;
import com.lois.tytool.TyToast;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 抽象BaseAppComatActivity类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Context mContext;
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        if (isNeedLayoutRes()) {
            setContentView(initContentView());
        }
        unbinder = ButterKnife.bind(this);
        initData(savedInstanceState);
        initView();
        ActivityManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        ActivityManager.getAppManager().finishActivity(this);
    }

    /**
     * 是否需要布局文件
     * @return 是否需要布局
     */
    protected boolean isNeedLayoutRes() {
        return true;
    }

    /**
     * 设置布局资源
     * @return 布局资源
     */
    protected abstract int initContentView();

    /**
     * 初始化Activity数据
     * @param savedInstanceState 实例状态
     */
    protected abstract void initData(Bundle savedInstanceState);

    /**
     * 初始化UI控件
     */
    protected abstract void initView();

    /**
     * 获取当前 Activity 对象
     */
    public BaseActivity getActivity() {
        return this;
    }

    /**
     * 和 setContentView 对应的方法
     */
    public ViewGroup getContentView() {
        return findViewById(Window.ID_ANDROID_CONTENT);
    }

    private void overridePendingTransition() {
        overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim_left);
    }

    /**
     * 页面跳转
     *
     * @param clz 要跳转的Activity
     */
    public void startActivity(Class<?> clz) {
        if (clz == null) {
            return;
        }
        startActivity(new Intent(this, clz));
        overridePendingTransition();
    }

    /**
     * 页面跳转
     *
     * @param clz    要跳转的Activity
     * @param intent intent
     */
    public void startActivity(Class<?> clz, Intent intent) {
        if (clz == null || intent == null) {
            return;
        }
        intent.setClass(this, clz);
        startActivity(intent);
        overridePendingTransition();
    }

    /**
     * 携带数据的页面跳转
     *
     * @param clz    要跳转的Activity
     * @param bundle 需要传递的数据
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        if (clz == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition();
    }

    /**
     * 页面跳转并要求获取返回结果
     *
     * @param clz         要跳转的Activity
     * @param requestCode requestCode
     */
    public void startActivityForResult(Class<?> clz, int requestCode) {
        if (clz == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, clz);
        startActivityForResult(intent, requestCode);
        overridePendingTransition();
    }

    /**
     * 页面跳转并要求获取返回结果
     *
     * @param clz       要跳转的Activity
     * @param intent    intent
     * @param requestCode requestCode
     */
    public void startActivityForResult(Class<?> clz, Intent intent, int requestCode) {
        if (clz == null || intent == null) {
            return;
        }
        intent.setClass(this, clz);
        startActivityForResult(intent, requestCode);
        overridePendingTransition();
    }

    /**
     * 页面跳转并要求获取返回结果
     *
     * @param clz         要跳转的Activity
     * @param bundle      bundle数据
     * @param requestCode requestCode
     */
    public void startActivityForResult(Class<?> clz, Bundle bundle, int requestCode) {
        if (clz == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
        overridePendingTransition();
    }

    /**
     * 吐司
     * @param text 文本
     */
    protected void showToast(String text) {
        TyToast.showShort(text);
    }

    /**
     * 顶部栏
     */
    protected Toolbar mToolbar;
    /**
     * 顶部栏居中标题
     */
    protected TextView mToolbarCenterTitle;
    /**
     * 顶部栏右边标题
     */
    protected TextView mToolbarRightTitle;

    /**
     * 初始化返回式顶部栏，需要在具体activity中调用
     */
    protected void initBackToolbar(){
        initToolbarView();
        if(mToolbar!=null){
            //调用etSupportActionBar后需要设置toolbar标题为空，不然就会显示app名称
            //mToolbar.setTitle("");
            //设置成为顶部栏，菜单才会出现
            //setSupportActionBar(mToolbar);
            //设置顶部栏的导航图标
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            //添加顶部栏的导航点击事件监听器
            setToolbarNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    /**
     * 初始化Toolbar控件
     */
    private void initToolbarView(){
        mToolbar=(Toolbar)findViewById(R.id.toolbar_back);
        mToolbarCenterTitle=(TextView) findViewById(R.id.toolbar_center_title);
        mToolbarRightTitle=(TextView)findViewById(R.id.toolbar_right_title);
        //调用etSupportActionBar后需要设置toolbar标题为空，不然就会显示app名称
        mToolbar.setTitle("");
        //设置成为顶部栏，菜单才会出现
        setSupportActionBar(mToolbar);
    }

    /**
     * 设置搜索式顶部栏
     */
    protected void initSearchToolbar(){
        initToolbarView();
        if(mToolbar!=null){
            //调用etSupportActionBar后需要设置toolbar标题为空，不然就会显示app名称
            //mToolbar.setTitle("");
            //设置成为顶部栏，菜单才会出现
            //setSupportActionBar(mToolbar);
        }
    }


    /**
     * 设置顶部栏的标题
     * @param title 标题
     */
    protected void setToolbarCenterTitle(CharSequence title){
        if(mToolbarCenterTitle!=null){
            mToolbarCenterTitle.setText(title);
        }else{
            initToolbarView();
            setToolbarCenterTitle(title);
        }
    }

    /**
     * 设置顶部栏的标题
     * @param resourceID 资源ID
     */
    protected void setToolbarCenterTitle(int resourceID){
        if(mToolbarCenterTitle!=null){
            mToolbarCenterTitle.setText(resourceID);
        }else{
            initToolbarView();
            setToolbarCenterTitle(resourceID);
        }
    }

    /**
     * 设置顶部栏标题的菜单监听器
     * @param menuItemClickListener
     */
    protected void setToolbarOnMenuItemClickListener(Toolbar.OnMenuItemClickListener menuItemClickListener){
        if(mToolbar!=null){
            mToolbar.setOnMenuItemClickListener(menuItemClickListener);
        }
    }

    /**
     * 设置顶部栏的导航图标
     * @param resourceID
     */
    protected void setToolbarNavigationIcon(int resourceID){
        if(mToolbar!=null){
            mToolbar.setNavigationIcon(resourceID);
        }
    }

    /**
     * 设置顶部栏导航的点击监听器
     * @param clickListener
     */
    protected void setToolbarNavigationOnClickListener(View.OnClickListener clickListener){
        if(mToolbar!=null){
            mToolbar.setNavigationOnClickListener(clickListener);
        }
    }

    /**
     * 设置顶部栏的右边标题
     * @param title 标题
     */
    protected void setToolbarRightTitle(CharSequence title){
        if(mToolbarRightTitle!=null){
            mToolbarRightTitle.setText(title);
        }
    }

    /**
     * 设置顶部导航栏的右边标题
     * @param resourceID
     */
    protected void setToolbarRightTitle(int resourceID){
        if(mToolbarRightTitle!=null){
            mToolbarRightTitle.setText(resourceID);
        }
    }
    /**
     * 设置顶部栏导航右边标题的点击监听器
     * @param clickListener
     */
    protected void setToolbarRightTitleOnClickListener(View.OnClickListener clickListener){
        if(mToolbarRightTitle!=null){
            mToolbarRightTitle.setOnClickListener(clickListener);
        }
    }

}
