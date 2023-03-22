package com.lois.tytool.base.stream.sticky;

import java.util.List;

/**
 * @Description TODO
 * @Author Luo.T.Y
 * @Date 2023/2/15
 * @Time 19:11
 */
public interface IStickPackage {
    /**
     * 粘包解包处理
     * @param data
     * @param len
     * @return
     */
    List<byte[]> unPack(byte[] data, int len);

    /**
     * 粘包打包处理
     * @param data
     * @param len
     * @return
     */
    byte[] doPack(byte[] data, int len);
}
