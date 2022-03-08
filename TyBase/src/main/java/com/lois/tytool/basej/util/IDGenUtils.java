package com.lois.tytool.basej.util;

import com.lois.tytool.basej.constant.FileConstants;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * id主键生成工具
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class IDGenUtils {

    private static AtomicLong next = new AtomicLong(1);

    /**
     * 获取32位的UUID
     * @return
     */
    public static String getUUID(){
        return UUID.randomUUID().toString().replace(FileConstants.MINUS_STR, FileConstants.BLANK_STR);
    }

    /**
     * 生成一个13位数的唯一id
     * @return
     */
    public static long getPKNum(){
        return next.getAndIncrement() + System.currentTimeMillis();
    }

    /**
     * 生成一个13位数的唯一id
     * @return
     */
    public static String getPKStr(){
        return String.valueOf(getPKNum());
    }
}
