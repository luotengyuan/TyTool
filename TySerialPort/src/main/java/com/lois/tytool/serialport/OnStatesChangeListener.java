package com.lois.tytool.serialport;

/**
 * @Description TODO
 * @Author Luo.T.Y
 * @Date 2023/2/20
 * @Time 17:30
 */
public interface OnStatesChangeListener {
    /**
     * 打开时的回调
     *
     * @param isSuccess 是否成功
     * @param reason    原因
     */
    void onOpen(boolean isSuccess, String reason);

    /**
     * 关闭时的回调
     */
    void onClose();
}
