package com.lois.tytool.base.stream.coder;

/**
 * @Description 消息接口
 * @Author Lois
 * @Date 2023/2/27 22:44
 */
public interface IMessage<T> {
    /**
     * 将完整的数据包解码成指定对象
     *
     * @return 解码后实体
     */
    T decode();

    /**
     * 将完整的数据包解码成指定对象
     * @param data  完整的数据包
     * @return
     */
    T decode(byte[] data);

    /**
     * 将指定对象编码成完整的数据包
     *
     * @return 编码后完整的数据包
     */
    byte[] encode();

    /**
     * 将指定对象编码成完整的数据包
     * @param bean  对象实体
     * @return
     */
    byte[] encode(T bean);
}
