package com.lois.tytool.base.stream.coder;

/**
 * @Description TODO
 * @Author Luo.T.Y
 * @Date 2023/2/15
 * @Time 19:54
 */
public interface IBytesCoder<T> {
    /**
     * 将完整的数据包解码成指定对象
     *
     * @param data 完整的数据包
     * @return 解码后实体
     */
    T decode(byte[] data);

    /**
     * 将指定对象编码成完整的数据包
     *
     * @param bean 实体
     * @return 编码后完整的数据包
     */
    byte[] encode(T bean);
}
