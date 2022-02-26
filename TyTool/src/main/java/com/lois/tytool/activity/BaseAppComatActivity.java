package com.lois.tytool.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lois.tytool.R;

/**
 * 抽象BaseAppComatActivity类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public abstract class BaseAppComatActivity extends AppCompatActivity {
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
    }

    /**
     * 初始化Activity
     */
    protected abstract void initActivity();

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 进入指定的Activity
     * @param cls
     */
    protected void startActivity(Class<?> cls){
        Intent intent=new Intent(mContext,cls);
        startActivity(intent);
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
