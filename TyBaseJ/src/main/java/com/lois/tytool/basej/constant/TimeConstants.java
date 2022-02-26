package com.lois.tytool.basej.constant;

/**
 * @Description TimeConstants
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public enum TimeConstants {

    MSEC(1),SEC(1000),MIN(60000),HOUR(3600000),DAY(86400000);

    public int val;

    TimeConstants(int val) {
        this.val = val;
    }
}
