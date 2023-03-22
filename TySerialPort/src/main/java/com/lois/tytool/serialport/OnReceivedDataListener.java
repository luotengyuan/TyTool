package com.lois.tytool.serialport;

/**
 * @Description 实现串口数据的接收监听
 * @Author Luo.T.Y
 * @Date 2023/2/20
 * @Time 17:30
 */
public interface OnReceivedDataListener {
    /**
     * 串口接收到数据后的回调
     *
     * @param data 接收到的数据
     * @param size 接收到的数据长度
     */
    void onReceivedData(byte[] data, int size);
}
