package com.lois.tytool.basej.secert.sm;

/**
 * <Sm4ContextUtils
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class Sm4ContextUtils {
    public int mode;

    public long[] sk;

    public boolean isPadding;

    public Sm4ContextUtils() {
        this.mode = 1;
        this.isPadding = true;
        this.sk = new long[32];
    }
}