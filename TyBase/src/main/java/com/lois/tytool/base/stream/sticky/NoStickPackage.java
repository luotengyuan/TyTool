package com.lois.tytool.base.stream.sticky;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 无粘包
 * @Author Luo.T.Y
 * @Date 2023/2/15
 * @Time 19:17
 */
public class NoStickPackage implements IStickPackage {
    @Override
    public List<byte[]> unPack(byte[] data, int len) {
        if (data == null || len <= 0) {
            return null;
        }
        List<byte[]> ret = new ArrayList<>();
        len = Math.min(data.length, len);
        byte[] result = new byte[len];
        System.arraycopy(data, 0, result, 0, len);
        ret.add(result);
        return ret;
    }

    @Override
    public byte[] doPack(byte[] data, int len) {
        if (data == null || len <= 0) {
            return null;
        }
        len = Math.min(data.length, len);
        byte[] result = new byte[len];
        System.arraycopy(data, 0, result, 0, len);
        return result;
    }
}
