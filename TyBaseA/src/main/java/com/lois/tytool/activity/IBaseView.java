package com.lois.tytool.activity;

/**
 * MVP框架模式View层的通用抽象接口，用于给其他View层接口继承。
 * 在MVP模式中，View层只应该有简单的set/get方法、用户输入和界面显示的内容，此外不应该有更多的内容。
 * 绝不允许View层直接访问Model。
 * <p></p>
 * IBaseView
 * 界面需要提供的UI方法中会有很多类似的UI方法，可以把它们提取到一个公共的父类接口中。
 * 比如提取显示Progress界面和隐藏Progress界面的方法，其他的view层接口就可以直接继承BaseView接口，
 * 不必重复的写显示和隐藏loading界面方法。
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public interface IBaseView {

    /**
     * 视图是否还在活动
     * @return
     */
    boolean isActive();

    /**
     * 显示消息
     * @param title
     * @param content
     */
    void showMessage(String title, String content);

    /**
     * 显示错误信息
     * @param title
     * @param content
     */
    void showErrorMessage(String title, String content);

    /**
     * 显示进度
     */
    void showProgress();

    /**
     * 隐藏进度
     */
    void dismissProgress();

    /**
     * 关闭视图
     */
    void closeView();

}
