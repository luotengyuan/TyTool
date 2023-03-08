package com.lois.tytool.base.stream.coder;

/**
 * @Description 消息接收接口
 * @Author Lois
 * @Date 2023/2/27 22:58
 */
public interface IMessageReceiver<T> extends IMessage<T> {
    /**
     * 接收数据
     * @param target    数据发送者信息
     * @param bean      发送的数据实例
     */
    void onReceiveData(Object target, T bean);
}
