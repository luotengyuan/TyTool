package com.lois.tytool.base.stream.sticky;

/**
 * @Description TODO
 * @Author Luo.T.Y
 * @Date 2023/2/15
 * @Time 19:17
 */
public class BaseStickPackage implements IStickPackage {
    @Override
    public byte[] execute(byte[] data, int len) {
        if (data == null || len <= 0) {
            return null;
        }
        len = data.length > len ? len : data.length;
        byte[] result = new byte[len];
        System.arraycopy(data, 0, result, 0, len);
        return result;
    }
}
